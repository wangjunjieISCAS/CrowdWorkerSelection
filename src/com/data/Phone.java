package com.data;

public class Phone {
	String phoneType;
	String OS;
	String network;
	String ISP;
	
	public Phone ( String phoneType, String OS, String network, String ISP) {
		this.phoneType = phoneType;
		this.OS = OS;
		this.network = network;
		this.ISP = ISP;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getOS() {
		return OS;
	}

	public void setOS(String oS) {
		OS = oS;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getISP() {
		return ISP;
	}

	public void setISP(String iSP) {
		ISP = iSP;
	}
	
	
}
