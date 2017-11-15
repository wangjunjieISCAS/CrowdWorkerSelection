package com.data;

public class Capability {
	Integer numProject;
	Integer numReport;
	Integer numBug;
	Double percBug;
	
	public Capability (Integer numProject, Integer numReport, Integer numBug, Double percBug ) {
		this.numProject = numProject;
		this.numReport = numReport;
		this.numBug = numBug;
		this.percBug = percBug;
	}

	public Integer getNumProject() {
		return numProject;
	}

	public void setNumProject(Integer numProject) {
		this.numProject = numProject;
	}

	public Integer getNumReport() {
		return numReport;
	}

	public void setNumReport(Integer numReport) {
		this.numReport = numReport;
	}

	public Integer getNumBug() {
		return numBug;
	}

	public void setNumBug(Integer numBug) {
		this.numBug = numBug;
	}

	public Double getPercBug() {
		return percBug;
	}

	public void setPercBug(Double percBug) {
		this.percBug = percBug;
	}
	
	
}
