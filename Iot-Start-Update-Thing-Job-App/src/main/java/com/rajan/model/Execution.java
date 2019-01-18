
package com.rajan.model;

public class Execution {

	private String jobId;
	private String status;
	private Integer queuedAt;
	private Integer startedAt;
	private Integer lastUpdatedAt;
	private Integer versionNumber;
	private Integer executionNumber;
	private Integer approximateSecondsBeforeTimedOut;
	private JobDocument jobDocument;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getQueuedAt() {
		return queuedAt;
	}

	public void setQueuedAt(Integer queuedAt) {
		this.queuedAt = queuedAt;
	}

	public Integer getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Integer startedAt) {
		this.startedAt = startedAt;
	}

	public Integer getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Integer lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Integer getExecutionNumber() {
		return executionNumber;
	}

	public void setExecutionNumber(Integer executionNumber) {
		this.executionNumber = executionNumber;
	}

	public Integer getApproximateSecondsBeforeTimedOut() {
		return approximateSecondsBeforeTimedOut;
	}

	public void setApproximateSecondsBeforeTimedOut(Integer approximateSecondsBeforeTimedOut) {
		this.approximateSecondsBeforeTimedOut = approximateSecondsBeforeTimedOut;
	}

	public JobDocument getJobDocument() {
		return jobDocument;
	}

	public void setJobDocument(JobDocument jobDocument) {
		this.jobDocument = jobDocument;
	}

}