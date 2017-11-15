package com.data;

//test case is the whole combination of test task, crowd worker, and the test oracle of this test combination
public class TestCase {
	TestTask task;
	CrowdWorker worker;
	String testOracle;
	
	public TestCase ( TestTask task, CrowdWorker worker, String testOracle ) {
		this.task = task;
		this.worker = worker;
		this.testOracle = testOracle;
	}

	public TestTask getTask() {
		return task;
	}

	public void setTask(TestTask task) {
		this.task = task;
	}

	public CrowdWorker getWorker() {
		return worker;
	}

	public void setWorker(CrowdWorker worker) {
		this.worker = worker;
	}

	public String getTestOracle() {
		return testOracle;
	}

	public void setTestOracle(String testOracle) {
		this.testOracle = testOracle;
	}
	
	
}
