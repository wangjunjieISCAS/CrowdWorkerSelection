package com.data;

public class TestReport {
	int id;
	
	String userId;
	
	String testCaseId;
	String testCaseName;
	
	String location;
	String phoneType;
	String OS;
	String network;
	String ISP;
	String ROM;
	
	String submitTime;
	
	String bugDetail;
	String reproSteps;
	
	String isKnown;
	String priority;
	String tag;

	public TestReport ( int id, String userId, String testCaseId, String testCaseName, String location, String phoneType, String OS, String network,
			String ISP, String ROM, String submitTime, String bugDetail, String reproSteps, String isKnown, String priority, String tag) {
		this.id = id;
		this.userId = userId;
		this.testCaseId = testCaseId;
		this.testCaseName = testCaseName;
		
		this.location = location;
		this.phoneType = phoneType;
		this.OS = OS;
		this.network = network;
		this.ISP = ISP;
		this.ROM = ROM;
		
		this.submitTime = submitTime;
		this.bugDetail = bugDetail;
		this.reproSteps = reproSteps;
		
		this.isKnown = isKnown;
		this.priority = priority;
		this.tag = tag;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getROM() {
		return ROM;
	}

	public void setROM(String rOM) {
		ROM = rOM;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getBugDetail() {
		return bugDetail;
	}

	public void setBugDetail(String bugDetail) {
		this.bugDetail = bugDetail;
	}

	public String getReproSteps() {
		return reproSteps;
	}

	public void setReproSteps(String reproSteps) {
		this.reproSteps = reproSteps;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIsKnown() {
		return isKnown;
	}

	public void setIsKnown(String isKnown) {
		this.isKnown = isKnown;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}	
	
	public String toString ( ) {
		String report = new Integer(id).toString() + " " + userId + " " + testCaseId + " " + testCaseName + " " + location + " " + phoneType
				+ " " + OS + " " + network + " " + ISP + " " + ROM + " " + 
				submitTime + " " + bugDetail + " " + reproSteps + " " + isKnown + " " + priority + " " + tag ;
		return report;
	}
}
