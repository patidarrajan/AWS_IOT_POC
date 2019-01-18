
package com.rajan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QUEUED {

    @SerializedName("jobId")
    @Expose
    private String jobId;
    @SerializedName("queuedAt")
    @Expose
    private Integer queuedAt;
    @SerializedName("lastUpdatedAt")
    @Expose
    private Integer lastUpdatedAt;
    @SerializedName("executionNumber")
    @Expose
    private Integer executionNumber;
    @SerializedName("versionNumber")
    @Expose
    private Integer versionNumber;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(Integer queuedAt) {
        this.queuedAt = queuedAt;
    }

    public Integer getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Integer lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Integer getExecutionNumber() {
        return executionNumber;
    }

    public void setExecutionNumber(Integer executionNumber) {
        this.executionNumber = executionNumber;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

}
