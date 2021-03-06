/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.adapters.link;

import org.opennms.netmgt.provision.adapters.link.endpoint.AndEndPointValidationExpression;
import org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl;
import org.opennms.netmgt.provision.adapters.link.endpoint.MatchingSnmpEndPointValidationExpression;
import org.opennms.netmgt.provision.adapters.link.endpoint.OrEndPointValidationExpression;
import org.opennms.netmgt.provision.adapters.link.endpoint.PingEndPointValidationExpression;

/**
 * <p>Abstract EndPointValidationExpressions class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public abstract class EndPointValidationExpressions {
    
    /**
     * <p>ping</p>
     *
     * @return a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     */
    public static EndPointValidationExpressionImpl ping() {
        return new PingEndPointValidationExpression();
    }
    
    /**
     * <p>match</p>
     *
     * @param oid a {@link java.lang.String} object.
     * @param regex a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     */
    public static EndPointValidationExpressionImpl match(final String oid, final String regex) {
        return new MatchingSnmpEndPointValidationExpression(regex, oid);
    }
    
    /**
     * <p>and</p>
     *
     * @param validators a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     * @return a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     */
    public static EndPointValidationExpressionImpl and(final EndPointValidationExpressionImpl... validators) {
        return new AndEndPointValidationExpression(validators);
    }
    
    /**
     * <p>or</p>
     *
     * @param validators a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     * @return a {@link org.opennms.netmgt.provision.adapters.link.endpoint.EndPointValidationExpressionImpl} object.
     */
    public static EndPointValidationExpressionImpl or(final EndPointValidationExpressionImpl... validators) {
        return new OrEndPointValidationExpression(validators);
    }
    
}
