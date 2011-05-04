/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2011 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */

package org.opennms.gwt.web.ui.asset.client.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.opennms.gwt.web.ui.asset.client.AssetPageConstants;
import org.opennms.gwt.web.ui.asset.client.presenter.AssetPagePresenter;
import org.opennms.gwt.web.ui.asset.client.tools.DisclosurePanelCookie;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSet;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSetDateBox;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSetListBox;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSetSuggestBox;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSetTextArea;
import org.opennms.gwt.web.ui.asset.client.tools.FieldSetTextBox;
import org.opennms.gwt.web.ui.asset.shared.AssetCommand;
import org.opennms.gwt.web.ui.asset.shared.AssetSuggCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:MarkusNeumannMarkus@gmail.com">Markus Neumann</a>
 * 
 */
public class AssetNodePageImpl extends Composite implements AssetPagePresenter.Display {

	private static AssetNodePageUiBinder uiBinder = GWT.create(AssetNodePageUiBinder.class);

	@UiTemplate("AssetNodePage.ui.xml")
	interface AssetNodePageUiBinder extends UiBinder<Widget, AssetNodePageImpl> {
	}

	private AssetPageConstants con = GWT.create(AssetPageConstants.class);

	AssetCommand m_asset;

	@UiField
	VerticalPanel mainPanel;

	@UiField
	Label lInfoTop;
	@UiField
	Label lInfoBottom;

	@UiField
	DisclosurePanelCookie snmpDiscPanel;

	@UiField
	Label lcSystemId;
	@UiField
	Label lcSystemName;
	@UiField
	Label lcSystemLocation;
	@UiField
	Label lcSystemContact;
	@UiField
	Label lcSystemDescription;

	@UiField
	FieldSetSuggestBox sDisplayCat;
	@UiField
	FieldSetSuggestBox sNotificationCat;
	@UiField
	FieldSetSuggestBox sPollerCat;
	@UiField
	FieldSetSuggestBox sThresholdCat;

	@UiField
	FieldSetSuggestBox sDescription;
	@UiField
	FieldSetSuggestBox sAssetCategory;
	@UiField
	FieldSetSuggestBox sManufacturer;
	@UiField
	FieldSetSuggestBox sModelNumber;
	@UiField
	FieldSetTextBox sSerialNumber;
	@UiField
	FieldSetTextBox sAssetNumber;
	@UiField
	FieldSetSuggestBox sOperatingSystem;
	@UiField
	FieldSetDateBox sDateInstalled;

	@UiField
	FieldSetSuggestBox sRegion;
	@UiField
	FieldSetSuggestBox sDivision;
	@UiField
	FieldSetSuggestBox sDepartment;
	@UiField
	FieldSetSuggestBox sAddress1;
	@UiField
	FieldSetSuggestBox sAddress2;
	@UiField
	FieldSetSuggestBox sCity;
	@UiField
	FieldSetSuggestBox sState;
	@UiField
	FieldSetSuggestBox sZip;
	@UiField
	FieldSetSuggestBox sBuilding;
	@UiField
	FieldSetSuggestBox sFloor;
	@UiField
	FieldSetSuggestBox sRoom;
	@UiField
	FieldSetSuggestBox sRack;
	@UiField
	FieldSetTextBox sSlot;
	@UiField
	FieldSetTextBox sPort;
	@UiField
	FieldSetSuggestBox sCircuitId;
	@UiField
	FieldSetSuggestBox sAdmin;

	@UiField
	FieldSetSuggestBox sVendorName;
	@UiField
	FieldSetSuggestBox sPhone;
	@UiField
	FieldSetSuggestBox sFax;
	@UiField
	FieldSetSuggestBox sLease;
	@UiField
	FieldSetDateBox sLeaseExpires;
	@UiField
	FieldSetTextBox sVendorAsset;
	@UiField
	FieldSetSuggestBox sMaintContract;
	@UiField
	FieldSetDateBox sContractExpires;
	@UiField
	FieldSetSuggestBox sMaintPhone;

	@UiField
	FieldSetTextBox sUserName;
	@UiField
	FieldSetTextBox sPassword;
	@UiField
	FieldSetTextBox sEnablePassword;
	@UiField
	FieldSetListBox sConnection;
	@UiField
	FieldSetListBox sAutoEnable;
	@UiField
	FieldSetSuggestBox sSnmpcommunity;

	@UiField
	FieldSetSuggestBox sCpu;
	@UiField
	FieldSetSuggestBox sRam;
	@UiField
	FieldSetSuggestBox sStoragectrl;
	@UiField
	FieldSetSuggestBox sAdditionalhardware;
	@UiField
	FieldSetSuggestBox sNumpowersupplies;
	@UiField
	FieldSetSuggestBox sInputpower;

	@UiField
	FieldSetSuggestBox sHdd1;
	@UiField
	FieldSetSuggestBox sHdd2;
	@UiField
	FieldSetSuggestBox sHdd3;
	@UiField
	FieldSetSuggestBox sHdd4;
	@UiField
	FieldSetSuggestBox sHdd5;
	@UiField
	FieldSetSuggestBox sHdd6;

	@UiField
	FieldSetTextArea sComment;

	@UiField
	Button saveButton;
	@UiField
	Button resetButton;

	@UiField
	Label lastModified;

	private ArrayList<FieldSet> fieldSetList = new ArrayList<FieldSet>();

	public AssetNodePageImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		initUiElementList();
	}

	private void initUiElementList() {
		fieldSetList.add(sDisplayCat);
		fieldSetList.add(sNotificationCat);
		fieldSetList.add(sPollerCat);
		fieldSetList.add(sThresholdCat);

		fieldSetList.add(sDescription);
		fieldSetList.add(sAssetCategory);
		fieldSetList.add(sManufacturer);
		fieldSetList.add(sModelNumber);
		fieldSetList.add(sSerialNumber);
		fieldSetList.add(sAssetNumber);
		fieldSetList.add(sDateInstalled);
		fieldSetList.add(sOperatingSystem);

		fieldSetList.add(sRegion);
		fieldSetList.add(sDivision);
		fieldSetList.add(sDepartment);
		fieldSetList.add(sAddress1);
		fieldSetList.add(sAddress2);
		fieldSetList.add(sCity);
		fieldSetList.add(sState);
		fieldSetList.add(sZip);
		fieldSetList.add(sBuilding);
		fieldSetList.add(sFloor);
		fieldSetList.add(sRoom);
		fieldSetList.add(sRack);
		fieldSetList.add(sSlot);
		fieldSetList.add(sPort);
		fieldSetList.add(sCircuitId);
		fieldSetList.add(sAdmin);

		fieldSetList.add(sVendorName);
		fieldSetList.add(sPhone);
		fieldSetList.add(sFax);
		fieldSetList.add(sLease);
		fieldSetList.add(sLeaseExpires);
		fieldSetList.add(sVendorAsset);
		fieldSetList.add(sMaintContract);
		fieldSetList.add(sContractExpires);
		fieldSetList.add(sMaintPhone);

		fieldSetList.add(sUserName);
		fieldSetList.add(sPassword);
		fieldSetList.add(sEnablePassword);
		fieldSetList.add(sConnection);
		fieldSetList.add(sAutoEnable);
		fieldSetList.add(sSnmpcommunity);

		fieldSetList.add(sCpu);
		fieldSetList.add(sRam);
		fieldSetList.add(sAdditionalhardware);
		fieldSetList.add(sInputpower);
		fieldSetList.add(sNumpowersupplies);
		fieldSetList.add(sStoragectrl);

		fieldSetList.add(sHdd1);
		fieldSetList.add(sHdd2);
		fieldSetList.add(sHdd3);
		fieldSetList.add(sHdd4);
		fieldSetList.add(sHdd5);
		fieldSetList.add(sHdd6);

		fieldSetList.add(sComment);
	}

	public void setData(AssetCommand asset) {
		this.m_asset = asset;
		if (m_asset.getLoggedInUser().equalsIgnoreCase("admin")){
			setEnable(true);
		}else{
			setEnable(false);
		}
		setDataSNMP(m_asset);
		setDataConfigCategories(m_asset);
		setDataIdentification(m_asset);
		setDataLocation(m_asset);
		setDataVendor(m_asset);
		setDataAuthentication(m_asset);
		setDataHardware(m_asset);
		setDataComments(m_asset);
		DateTimeFormat m_formater = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		lastModified
				.setText(con.lastModified() + " " + m_formater.format(asset.getLastModifiedDate()) + " | " + asset.getLastModifiedBy());
	}

	private void setDataSNMP(AssetCommand asset) {

		if ((asset.getSnmpSysObjectId().equals("")) || (asset.getSnmpSysObjectId() == null)) {
			snmpDiscPanel.setVisible(false);
		} else {
			lcSystemId.setText(asset.getSnmpSysObjectId());
			lcSystemName.setText(asset.getSnmpSysName());
			lcSystemLocation.setText(asset.getSnmpSysLocation());
			lcSystemContact.setText(asset.getSnmpSysContact());
			lcSystemDescription.setText(asset.getSnmpSysDescription());
			snmpDiscPanel.setVisible(true);
		}
	}

	private void setDataConfigCategories(AssetCommand asset) {
		sDisplayCat.setValue(asset.getDisplayCategory());
		sNotificationCat.setValue(asset.getNotifyCategory());
		sPollerCat.setValue(asset.getPollerCategory());
		sThresholdCat.setValue(asset.getThresholdCategory());
	}

	private void setDataIdentification(AssetCommand asset) {
		sDescription.setValue(asset.getDescription());
		sAssetCategory.setValue(asset.getCategory());
		sManufacturer.setValue(asset.getManufacturer());
		sModelNumber.setValue(asset.getModelNumber());
		sSerialNumber.setValue(asset.getSerialNumber());
		sAssetNumber.setValue(asset.getAssetNumber());
		sOperatingSystem.setValue(asset.getOperatingSystem());
		sDateInstalled.setValue(asset.getDateInstalled());
	}

	private void setDataLocation(AssetCommand asset) {
		sRegion.setValue(asset.getRegion());
		sDivision.setValue(asset.getDivision());
		sDepartment.setValue(asset.getDepartment());
		sAddress1.setValue(asset.getAddress1());
		sAddress2.setValue(asset.getAddress2());
		sCity.setValue(asset.getCity());
		sState.setValue(asset.getState());
		sZip.setValue(asset.getZip());
		sBuilding.setValue(asset.getBuilding());
		sFloor.setValue(asset.getFloor());
		sRoom.setValue(asset.getRoom());
		sRack.setValue(asset.getRack());
		sSlot.setValue(asset.getSlot());
		sPort.setValue(asset.getPort());
		sCircuitId.setValue(asset.getCircuitId());
		sAdmin.setValue(asset.getAdmin());
	}

	private void setDataVendor(AssetCommand asset) {
		sVendorName.setValue(asset.getVendor());
		sPhone.setValue(asset.getVendorPhone());
		sFax.setValue(asset.getVendorFax());
		sLease.setValue(asset.getLease());
		sLeaseExpires.setValue(asset.getLeaseExpires());
		sVendorAsset.setValue(asset.getVendorAssetNumber());
		sMaintContract.setValue(asset.getMaintContractNumber());
		sContractExpires.setValue(asset.getMaintContractExpiration());
		sMaintPhone.setValue(asset.getSupportPhone());
	}

	private void setDataAuthentication(AssetCommand asset) {
		sUserName.setValue(asset.getUsername());
		sPassword.setValue(asset.getPassword());
		sEnablePassword.setValue(asset.getEnable());
		sConnection.setOptions(asset.getConnectionOptions());
		sConnection.setValue(asset.getConnection());
		sAutoEnable.setOptions(asset.getAutoenableOptions());
		sAutoEnable.setValue(asset.getAutoenable());
		sSnmpcommunity.setValue(asset.getSnmpcommunity());
	}

	private void setDataHardware(AssetCommand asset) {
		sCpu.setValue(asset.getCpu());
		sRam.setValue(asset.getRam());
		sStoragectrl.setValue(asset.getStoragectrl());
		sAdditionalhardware.setValue(asset.getAdditionalhardware());
		sNumpowersupplies.setValue(asset.getNumpowersupplies());
		sInputpower.setValue(asset.getInputpower());
		sHdd1.setValue(asset.getHdd1());
		sHdd2.setValue(asset.getHdd2());
		sHdd3.setValue(asset.getHdd3());
		sHdd4.setValue(asset.getHdd4());
		sHdd5.setValue(asset.getHdd5());
		sHdd6.setValue(asset.getHdd6());
	}

	private void setDataComments(AssetCommand asset) {
		sComment.setValue(asset.getComment());
	}

	public AssetCommand getData() {
		saveDataConfigCategories();
		saveDataIdentification();
		saveDataLocation();
		saveDataVendor();
		saveDataAuthentication();
		saveDataHardware();
		saveDataComments();

		return m_asset;
	}

	private void saveDataConfigCategories() {
		m_asset.setDisplayCategory(sDisplayCat.getValue());
		m_asset.setNotifyCategory(sNotificationCat.getValue());
		m_asset.setPollerCategory(sPollerCat.getValue());
		m_asset.setThresholdCategory(sThresholdCat.getValue());
	}

	private void saveDataIdentification() {
		m_asset.setDescription(sDescription.getValue());
		m_asset.setCategory(sAssetCategory.getValue());
		m_asset.setManufacturer(sManufacturer.getValue());
		m_asset.setModelNumber(sModelNumber.getValue());
		m_asset.setSerialNumber(sSerialNumber.getValue());
		m_asset.setAssetNumber(sAssetNumber.getValue());
		m_asset.setOperatingSystem(sOperatingSystem.getValue());
		m_asset.setDateInstalled(sDateInstalled.getValue());
	}

	private void saveDataLocation() {
		m_asset.setRegion(sRegion.getValue());
		m_asset.setDivision(sDivision.getValue());
		m_asset.setDepartment(sDepartment.getValue());
		m_asset.setAddress1(sAddress1.getValue());
		m_asset.setAddress2(sAddress2.getValue());
		m_asset.setCity(sCity.getValue());
		m_asset.setState(sState.getValue());
		m_asset.setZip(sZip.getValue());
		m_asset.setBuilding(sBuilding.getValue());
		m_asset.setFloor(sFloor.getValue());
		m_asset.setRoom(sRoom.getValue());
		m_asset.setRack(sRack.getValue());
		m_asset.setSlot(sSlot.getValue());
		m_asset.setPort(sPort.getValue());
		m_asset.setCircuitId(sCircuitId.getValue());
		m_asset.setAdmin(sAdmin.getValue());
	}

	private void saveDataVendor() {
		m_asset.setVendor(sVendorName.getValue());
		m_asset.setVendorPhone(sPhone.getValue());
		m_asset.setVendorFax(sFax.getValue());
		m_asset.setLease(sLease.getValue());
		m_asset.setLeaseExpires(sLeaseExpires.getValue());
		m_asset.setVendorAssetNumber(sVendorAsset.getValue());
		m_asset.setMaintContractNumber(sMaintContract.getValue());
		m_asset.setMaintContractExpiration(sContractExpires.getValue());
		m_asset.setSupportPhone(sMaintPhone.getValue());
	}

	private void saveDataAuthentication() {
		m_asset.setUsername(sUserName.getValue());
		m_asset.setPassword(sPassword.getValue());
		m_asset.setEnable(sEnablePassword.getValue());
		m_asset.setConnection(sConnection.getValue());
		m_asset.setAutoenable(sAutoEnable.getValue());
		m_asset.setSnmpcommunity(sSnmpcommunity.getValue());
	}

	private void saveDataHardware() {
		m_asset.setCpu(sCpu.getValue());
		m_asset.setRam(sRam.getValue());
		m_asset.setStoragectrl(sStoragectrl.getValue());
		m_asset.setAdditionalhardware(sAdditionalhardware.getValue());
		m_asset.setNumpowersupplies(sNumpowersupplies.getValue());
		m_asset.setInputpower(sInputpower.getValue());

		m_asset.setHdd1(sHdd1.getValue());
		m_asset.setHdd2(sHdd2.getValue());
		m_asset.setHdd3(sHdd3.getValue());
		m_asset.setHdd4(sHdd4.getValue());
		m_asset.setHdd5(sHdd5.getValue());
		m_asset.setHdd6(sHdd6.getValue());
	}

	private void saveDataComments() {
		m_asset.setComment(sComment.getValue());
	}

	public void setDataSugg(AssetSuggCommand assetSugg) {
		setDataSuggConfigCategories(assetSugg);
		setDataSuggIdentification(assetSugg);
		setDataSuggLocation(assetSugg);
		setDataSuggVendor(assetSugg);
		setDataSuggAuth(assetSugg);
		setDataSuggHardware(assetSugg);
	}

	private void setDataSuggConfigCategories(AssetSuggCommand assetSugg) {
		sDisplayCat.setSuggestions(assetSugg.getDisplayCategory());
		sNotificationCat.setSuggestions(assetSugg.getNotifyCategory());
		sPollerCat.setSuggestions(assetSugg.getPollerCategory());
		sThresholdCat.setSuggestions(assetSugg.getThresholdCategory());
	}

	private void setDataSuggIdentification(AssetSuggCommand assetSugg) {
		sDescription.setSuggestions(assetSugg.getDescription());
		sAssetCategory.setSuggestions(assetSugg.getCategory());
		sManufacturer.setSuggestions(assetSugg.getManufacturer());
		sModelNumber.setSuggestions(assetSugg.getModelNumber());
		sOperatingSystem.setSuggestions(assetSugg.getOperatingSystem());
	}

	private void setDataSuggLocation(AssetSuggCommand assetSugg) {
		sRegion.setSuggestions(assetSugg.getRegion());
		sDivision.setSuggestions(assetSugg.getDivision());
		sDepartment.setSuggestions(assetSugg.getDepartment());
		sAddress1.setSuggestions(assetSugg.getAddress1());
		sAddress2.setSuggestions(assetSugg.getAddress2());
		sCity.setSuggestions(assetSugg.getCity());
		sState.setSuggestions(assetSugg.getState());
		sZip.setSuggestions(assetSugg.getZip());
		sBuilding.setSuggestions(assetSugg.getBuilding());
		sFloor.setSuggestions(assetSugg.getFloor());
		sRoom.setSuggestions(assetSugg.getRoom());
		sRack.setSuggestions(assetSugg.getRack());
		sCircuitId.setSuggestions(assetSugg.getCircuitId());
		sAdmin.setSuggestions(assetSugg.getAdmin());
	}

	private void setDataSuggVendor(AssetSuggCommand assetSugg) {
		sVendorName.setSuggestions(assetSugg.getVendor());
		sPhone.setSuggestions(assetSugg.getVendorPhone());
		sFax.setSuggestions(assetSugg.getVendorFax());
		sLease.setSuggestions(assetSugg.getLease());
		sMaintContract.setSuggestions(assetSugg.getMaintContractNumber());
		sMaintPhone.setSuggestions(assetSugg.getSupportPhone());
	}

	private void setDataSuggAuth(AssetSuggCommand assetSugg) {
		sSnmpcommunity.setSuggestions(assetSugg.getSnmpcommunity());
	}

	private void setDataSuggHardware(AssetSuggCommand assetSugg) {
		sCpu.setSuggestions(assetSugg.getCpu());
		sRam.setSuggestions(assetSugg.getRam());
		sStoragectrl.setSuggestions(assetSugg.getStoragectrl());
		sAdditionalhardware.setSuggestions(assetSugg.getAdditionalhardware());
		sNumpowersupplies.setSuggestions(assetSugg.getNumpowersupplies());
		sInputpower.setSuggestions(assetSugg.getInputpower());
		sHdd1.setSuggestions(assetSugg.getHdd1());
		sHdd2.setSuggestions(assetSugg.getHdd2());
		sHdd3.setSuggestions(assetSugg.getHdd3());
		sHdd4.setSuggestions(assetSugg.getHdd4());
		sHdd5.setSuggestions(assetSugg.getHdd5());
		sHdd6.setSuggestions(assetSugg.getHdd6());
	}

	public void setInfo(String info) {
		lInfoTop.setText(info);
		lInfoBottom.setText(info);
	}

	public Widget asWidget() {
		return this;
	}

	public void setEnable(Boolean enabled) {
		for (Iterator<FieldSet> fieldSetIter = fieldSetList.iterator(); fieldSetIter.hasNext();) {
			fieldSetIter.next().setEnabled(enabled);
		}
		saveButton.setEnabled(enabled);
		resetButton.setEnabled(enabled);
	}

	public void cleanUp() {
		for (Iterator<FieldSet> fieldSetIter = fieldSetList.iterator(); fieldSetIter.hasNext();) {
			FieldSet fs = fieldSetIter.next();
			fs.clearChanged();
			// fs.clearError();
		}
	}

	public HasClickHandlers getSaveButton() {
		return saveButton;
	}

	public HasClickHandlers getResetButton() {
		return resetButton;
	}
}
