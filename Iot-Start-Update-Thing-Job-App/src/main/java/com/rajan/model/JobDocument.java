
package com.rajan.model;

public class JobDocument {

    private String operation;
    private String firmwareUrl;
    private String certificatePem;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFirmwareUrl() {
        return firmwareUrl;
    }

    public void setFirmwareUrl(String otherInfo) {
        this.firmwareUrl = otherInfo;
    }

	public String getCertificatePem() {
		return certificatePem;
	}

	public void setCertificatePem(String certificatePem) {
		this.certificatePem = certificatePem;
	}
 
}