package com.data;

public class Capability {
	Integer[] numProject;
	Integer[] numReport;
	Integer[] numBug;
	Double[] percBug;
	Integer durationLastAct;   //距离上一次提交的天数
	
	public Capability (Integer[] numProject, Integer[] numReport, Integer[] numBug, Double[] percBug , Integer durationLastAct) {
		this.numProject = numProject;
		this.numReport = numReport;
		this.numBug = numBug;
		this.percBug = percBug;
		this.durationLastAct = durationLastAct;
	}

	public Integer[] getNumProject() {
		return numProject;
	}

	public void setNumProject(Integer[] numProject) {
		this.numProject = numProject;
	}

	public Integer[] getNumReport() {
		return numReport;
	}

	public void setNumReport(Integer[] numReport) {
		this.numReport = numReport;
	}

	public Integer[] getNumBug() {
		return numBug;
	}

	public void setNumBug(Integer[] numBug) {
		this.numBug = numBug;
	}

	public Double[] getPercBug() {
		return percBug;
	}

	public void setPercBug(Double[] percBug) {
		this.percBug = percBug;
	}

	public Integer getDurationLastAct() {
		return durationLastAct;
	}

	public void setDurationLastAct(Integer durationLastAct) {
		this.durationLastAct = durationLastAct;
	}
	
	
}
