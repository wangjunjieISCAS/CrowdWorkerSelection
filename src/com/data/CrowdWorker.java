package com.data;

import java.util.ArrayList;

//mobile context, worker's capability, domain knowledge, all together is the crowd worker

public class CrowdWorker {
	String workerId;
	Phone phoneInfo;
	Capability capInfo;
	DomainKnowledge domainKnInfo;
	
	public CrowdWorker ( String workerId, Phone phoneInfo, Capability capInfo, DomainKnowledge domainKnInfo) {
		this.workerId = workerId;
		this.phoneInfo = phoneInfo;
		this.capInfo = capInfo;
		this.domainKnInfo = domainKnInfo;
	}

	
	public String getWorkerId() {
		return workerId;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	public Phone getPhoneInfo() {
		return phoneInfo;
	}

	public void setPhoneInfo(Phone phoneInfo) {
		this.phoneInfo = phoneInfo;
	}

	public Capability getCapInfo() {
		return capInfo;
	}

	public void setCapInfo(Capability capInfo) {
		this.capInfo = capInfo;
	}

	public DomainKnowledge getDomainKnInfo() {
		return domainKnInfo;
	}

	public void setDomainKnInfo(DomainKnowledge domainKnInfo) {
		this.domainKnInfo = domainKnInfo;
	}
}
