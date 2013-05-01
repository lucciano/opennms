package org.opennms.netmgt.enlinkd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.opennms.core.utils.LogUtils;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.TopologyDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.model.topology.BridgeDot1dTpFdbLink;
import org.opennms.netmgt.model.topology.BridgeDot1qTpFdbLink;
import org.opennms.netmgt.model.topology.BridgeElementIdentifier;
import org.opennms.netmgt.model.topology.BridgeEndPoint;
import org.opennms.netmgt.model.topology.BridgeStpLink;
import org.opennms.netmgt.model.topology.CdpElementIdentifier;
import org.opennms.netmgt.model.topology.CdpEndPoint;
import org.opennms.netmgt.model.topology.CdpLink;
import org.opennms.netmgt.model.topology.Element;
import org.opennms.netmgt.model.topology.ElementIdentifier;
import org.opennms.netmgt.model.topology.EndPoint;
import org.opennms.netmgt.model.topology.InetElementIdentifier;
import org.opennms.netmgt.model.topology.Link;
import org.opennms.netmgt.model.topology.LldpElementIdentifier;
import org.opennms.netmgt.model.topology.LldpEndPoint;
import org.opennms.netmgt.model.topology.LldpLink;
import org.opennms.netmgt.model.topology.MacAddrElementIdentifier;
import org.opennms.netmgt.model.topology.MacAddrEndPoint;
import org.opennms.netmgt.model.topology.OspfElementIdentifier;
import org.opennms.netmgt.model.topology.OspfEndPoint;
import org.opennms.netmgt.model.topology.OspfLink;
import org.opennms.netmgt.model.topology.PseudoBridgeLink;
import org.opennms.netmgt.model.topology.PseudoMacLink;
import org.springframework.beans.factory.annotation.Autowired;

public class EnhancedLinkdServiceImpl implements EnhancedLinkdService {
	
	@Autowired
	private NodeDao m_nodeDao;

	@Autowired
	private TopologyDao m_topologyDao;

	public NodeDao getNodeDao() {
		return m_nodeDao;
	}

	public void setNodeDao(NodeDao nodeDao) {
		m_nodeDao = nodeDao;
	}

	public TopologyDao getTopologyDao() {
		return m_topologyDao;
	}

	public void setTopologyDao(TopologyDao topologyDao) {
		m_topologyDao = topologyDao;
	}

	
	@Override
	public List<LinkableNode> getSnmpNodeList() {
		final List<LinkableNode> nodes = new ArrayList<LinkableNode>();
		
		final OnmsCriteria criteria = new OnmsCriteria(OnmsNode.class);
        criteria.createAlias("ipInterfaces", "iface", OnmsCriteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("type", "A"));
        criteria.add(Restrictions.eq("iface.isSnmpPrimary", PrimaryType.PRIMARY));
        for (final OnmsNode node : m_nodeDao.findMatching(criteria)) {
            final String sysObjectId = node.getSysObjectId();
            nodes.add(new LinkableNode(node.getId(), node.getPrimaryInterface().getIpAddress(), sysObjectId == null? "-1" : sysObjectId));
        }

        return nodes;
	}

	@Override
	public LinkableNode getSnmpNode(final int nodeid) {
		final OnmsCriteria criteria = new OnmsCriteria(OnmsNode.class);
        criteria.createAlias("ipInterfaces", "iface", OnmsCriteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("type", "A"));
        criteria.add(Restrictions.eq("iface.isSnmpPrimary", PrimaryType.PRIMARY));
        criteria.add(Restrictions.eq("id", nodeid));
        final List<OnmsNode> nodes = m_nodeDao.findMatching(criteria);

        if (nodes.size() > 0) {
        	final OnmsNode node = nodes.get(0);
        	final String sysObjectId = node.getSysObjectId();
			return new LinkableNode(node.getId(), node.getPrimaryInterface().getIpAddress(), sysObjectId == null? "-1" : sysObjectId);
        } else {
        	return null;
        }
	}

	@Override
	public void reconcile() {
		Collection<Integer> nodeIds = m_nodeDao.getNodeIds();
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier elemId: e.getElementIdentifiers()) {
				if (!nodeIds.contains(elemId.getSourceNode()))
					elementidentifiers.add(elemId);
			}
			for (EndPoint endpoint: e.getEndpoints()) {
				if (!nodeIds.contains(endpoint.getSourceNode()))
					endpoints.add(endpoint);
			}
		}
		
		m_topologyDao.delete(elementidentifiers,endpoints);
		
	}

	@Override
	public void reconcile(int nodeid) {
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier elemId: e.getElementIdentifiers()) {
				if (nodeid == elemId.getSourceNode())
					elementidentifiers.add(elemId);
			}
			for (EndPoint endpoint: e.getEndpoints()) {
				if  (nodeid == endpoint.getSourceNode())
					endpoints.add(endpoint);
			}
		}
		
		m_topologyDao.delete(elementidentifiers,endpoints);
	}

	@Override
	public void store(LldpLink link) {
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(CdpLink link) {
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(OspfLink link) {
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(MacAddrEndPoint endpoint) {
		m_topologyDao.saveOrUpdate(endpoint);
	}
 
	@Override
	public void store(BridgeDot1dTpFdbLink link) {
		/*
		 * store(link=(({bridge port, mac}))
		 * This is a standalone mac address found
		 * must be saved in any case.
		 * 
		 * If mac address is found on some pseudo device must be removed
		 * the information must be checked to get the topology layout
		 * 
		 */
		List<EndPoint> topoendpoints = m_topologyDao.get(link.getB());
		if (topoendpoints.isEmpty()) {
			m_topologyDao.saveOrUpdate(link);
			return;
		}
		
		if (topoendpoints.size() != 1) {
	    	LogUtils.errorf(this,
	                "store:Aborting store Dot1d Link %s: more then one mac endpoint found", link);
	    	return;
		}
		
		MacAddrEndPoint topologyMacAddrEndPoint = (MacAddrEndPoint) topoendpoints
				.get(0);
		if (topologyMacAddrEndPoint.hasLink() && topologyMacAddrEndPoint.getLink() instanceof PseudoBridgeLink) {
			// FIXME get the backbone port to this device
			m_topologyDao.delete(topologyMacAddrEndPoint.getLink());
		}	
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(BridgeDot1qTpFdbLink link) {
		/*
		 * store(link=(({bridge port, mac}))
		 * This is a standalone mac address found
		 * must be saved in any case.
		 * 
		 * If mac address is found on some pseudo device must be removed
		 * the information must be checked to get the topology layout
		 * 
		 */
		List<EndPoint> topoendpoints = m_topologyDao.get(link.getB());
		if (topoendpoints.isEmpty()) {
			m_topologyDao.saveOrUpdate(link);
			return;
		}
		
		if (topoendpoints.size() != 1) {
	    	LogUtils.errorf(this,
	                "store:Aborting store Dot1d Link %s: more then one mac endpoint found", link);
	    	return;
		}
		
		MacAddrEndPoint topologyMacAddrEndPoint = (MacAddrEndPoint) topoendpoints
				.get(0);
		if (topologyMacAddrEndPoint.hasLink() && topologyMacAddrEndPoint.getLink() instanceof PseudoBridgeLink) {
			// FIXME get the backbone port to this device
			m_topologyDao.delete(topologyMacAddrEndPoint.getLink());
		}	
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(BridgeStpLink link) {
	
		/*
		 * store(link=({bridge port a},{bridge port b}))
		 * 
		 * this must be saved always.
		 * Before doing this you have to check if the "designated bridge"
		 * that is {bridge port b} is connected to some pseudo device
		 * in this case you have to remove the pseudo device
		 * 
		 */
		List<EndPoint> topoendpoints = m_topologyDao.get(link.getB());
		
		if (topoendpoints.isEmpty()) {
			m_topologyDao.saveOrUpdate(link);
			return;
		}
		
		if (topoendpoints.size() != 1) {
	    	LogUtils.errorf(this,
	                "store:Aborting store Bridge Stp Link %s: more then one bridge endpoint found", link);
	    	return;
		}
		
		BridgeEndPoint topologyBridgeEndPoint = (BridgeEndPoint) topoendpoints
				.get(0);
		if (topologyBridgeEndPoint.hasLink() && topologyBridgeEndPoint.getLink() instanceof PseudoBridgeLink) {
			m_topologyDao.delete(topologyBridgeEndPoint.getLink().getA().getElement());
		}	
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(PseudoBridgeLink link) {
		/* 
		 * store(link=({pseudo bridge port},{bridge port}))
		 * 
		 * pseudo code:
		 * 
		 * ({bridge port}) is not in topology)
		 *   save(link)
		 * 
		 * ({bridge port} is in topology)
		 * 
		 *  databasebridgeport={db bridge port}
		 * 
		 *  databasebridgeport.getLink() is StpLink
		 *     return
		 *     
		 *  databasebridgeport.getLink is PseudoBridgeLink
		 *     save(link)
		 *     
		 *  databasebridgeport.getLink is MacAddressLink 
		 *     delete old link
		 *     save(link)
		 * 
		 */
		
		List<EndPoint> topoendpoints = m_topologyDao.get(link.getB());
		
		if (topoendpoints.isEmpty()) {
			m_topologyDao.saveOrUpdate(link);
			return;
		}
		
		if (topoendpoints.size() != 1) {
	    	LogUtils.errorf(this,
	                "store:Aborting store Pseudo Link %s: more then one bridge endpoint found", link);
	    	return;
		}
		
		BridgeEndPoint topologyBridgeEndPoint = (BridgeEndPoint) topoendpoints
				.get(0);
		if (topologyBridgeEndPoint.hasLink()) {
			if (topologyBridgeEndPoint.getLink() instanceof BridgeStpLink) {
		    	LogUtils.infof(this,
		                "store:Not saving Pseudo Link %s: bridge stp link %s found", link, topologyBridgeEndPoint.getLink());
				return;
			} else if (topologyBridgeEndPoint.getLink() instanceof BridgeDot1qTpFdbLink
					|| topologyBridgeEndPoint.getLink() instanceof BridgeDot1qTpFdbLink) {
		    	LogUtils.infof(this,
		                "store:Pseudo Link %s: deleting old bridge direct link %s", link, topologyBridgeEndPoint.getLink());
				m_topologyDao.delete(topologyBridgeEndPoint.getLink());
			}
		}
		m_topologyDao.saveOrUpdate(link);
	}

	@Override
	public void store(PseudoMacLink link) {
		/*
		 * store(link=(({bridge port, mac}))
		 * This is a standalone mac address found
		 * must be saved in any case.
		 * 
		 * If mac address is found on some pseudo device must be removed
		 * the information must be checked to get the topology layout
		 * 
		 */
		List<EndPoint> topoendpoints = m_topologyDao.get(link.getB());
		if (topoendpoints.isEmpty()) {
			m_topologyDao.saveOrUpdate(link);
			return;
		}
		
		if (topoendpoints.size() != 1) {
	    	LogUtils.errorf(this,
	                "store:Aborting store Dot1d Link %s: more then one mac endpoint found", link);
	    	return;
		}
		
		MacAddrEndPoint topologyMacAddrEndPoint = (MacAddrEndPoint) topoendpoints
				.get(0);
		if (topologyMacAddrEndPoint.hasLink()
				&& (topologyMacAddrEndPoint.getLink() instanceof BridgeDot1qTpFdbLink || topologyMacAddrEndPoint
						.getLink() instanceof BridgeDot1qTpFdbLink)) {
			return;
		}
		//FIXME check the topology
		// this means that we should look at where is the macaddress found
		// topoendpoint.getLink is pseudoLink
		checkTopology(link,topologyMacAddrEndPoint.getLink());
		m_topologyDao.saveOrUpdate(link);
	}

	protected void  checkTopology(Link link1,Link link2) {
	//	BridgeCompatiblePath path1 = new BridgeCompatiblePath(mac, port1, port2)
	}

	@Override
	public void reconcileLldp(int nodeid, Date now) {

		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier elemId: e.getElementIdentifiers()) {
				if (nodeid == elemId.getSourceNode() && elemId.getLastPoll().before(now) && elemId instanceof LldpElementIdentifier)
					elementidentifiers.add(elemId);
			}
			for (EndPoint endpoint: e.getEndpoints()) {
				if  (nodeid == endpoint.getSourceNode() && endpoint.getLastPoll().before(now) && endpoint instanceof LldpEndPoint)
					endpoints.add(endpoint);
			}
		}
		
		m_topologyDao.delete(elementidentifiers,endpoints);

	}

	@Override
	public void reconcileCdp(int nodeid, Date now) {
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier elemId: e.getElementIdentifiers()) {
				if (nodeid == elemId.getSourceNode() && elemId.getLastPoll().before(now) && elemId instanceof CdpElementIdentifier)
					elementidentifiers.add(elemId);
			}
			for (EndPoint endpoint: e.getEndpoints()) {
				if  (nodeid == endpoint.getSourceNode() && endpoint.getLastPoll().before(now) && endpoint instanceof CdpEndPoint)
					endpoints.add(endpoint);
			}
		}
		
		m_topologyDao.delete(elementidentifiers,endpoints);
	}

	@Override
	public void reconcileOspf(int nodeid, Date now) {
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier elemId: e.getElementIdentifiers()) {
				if (nodeid == elemId.getSourceNode() && elemId.getLastPoll().before(now) && elemId instanceof OspfElementIdentifier)
					elementidentifiers.add(elemId);
			}
			for (EndPoint endpoint: e.getEndpoints()) {
				if  (nodeid == endpoint.getSourceNode() && endpoint.getLastPoll().before(now) && endpoint instanceof OspfEndPoint)
					endpoints.add(endpoint);
			}
		}
		
		m_topologyDao.delete(elementidentifiers,endpoints);
	}

	@Override
	public void reconcileIpNetToMedia(int nodeid, Date now) {
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier ei: e.getElementIdentifiers()) {
				if (nodeid == ei.getSourceNode() && ei.getLastPoll().before(now)) {
					if (ei instanceof MacAddrElementIdentifier) {
						elementidentifiers.add(ei);
					} else if (ei instanceof InetElementIdentifier) {
						elementidentifiers.add(ei);
					}
				}
			}
			
			for (EndPoint ep: e.getEndpoints()) {
				if ( nodeid == ep.getSourceNode()  && ep.getLastPoll().before(now) && ep instanceof MacAddrEndPoint) {
						endpoints.add(ep);
				}
			}
		}

		m_topologyDao.delete(elementidentifiers,endpoints);

	}
	
	@Override
	public void reconcileBridge(int nodeid, Date now) {
		List<ElementIdentifier> elementidentifiers = new ArrayList<ElementIdentifier>();
		List<EndPoint> endpoints = new ArrayList<EndPoint>();
		for (Element e: m_topologyDao.getTopology()) {
			for (ElementIdentifier ei: e.getElementIdentifiers()) {
				if (nodeid == ei.getSourceNode() && ei.getLastPoll().before(now)) {
					if (ei instanceof MacAddrElementIdentifier) {
						elementidentifiers.add(ei);
					} else if (ei instanceof BridgeElementIdentifier) {
						elementidentifiers.add(ei);
					}
				}
			}
			
			for (EndPoint ep: e.getEndpoints()) {
				if ( nodeid == ep.getSourceNode()  && ep.getLastPoll().before(now)) {
					if ( ep instanceof MacAddrEndPoint) {
						endpoints.add(ep);
					} else if (ep instanceof BridgeEndPoint) {
						endpoints.add(ep);
					} 
				}
			}
		}

		m_topologyDao.delete(elementidentifiers,endpoints);

	}
}
