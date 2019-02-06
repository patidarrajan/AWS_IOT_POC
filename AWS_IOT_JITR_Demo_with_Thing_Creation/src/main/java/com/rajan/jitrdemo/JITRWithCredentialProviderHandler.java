package com.rajan.jitrdemo;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.CertificateStatus;
import com.amazonaws.services.iot.model.CreatePolicyRequest;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.DescribeCertificateRequest;
import com.amazonaws.services.iot.model.DescribeCertificateResult;
import com.amazonaws.services.iot.model.UpdateCertificateRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class JITRWithCredentialProviderHandler implements RequestHandler<RequestClass, String> {
	private Context context;
	private AWSIot client;
	private static final String POLICY_PREFIX = "POLICY_";
	private static final String CREDENTIAL_PROVIDER_RULE_ALIAS = "XXXXXXXXXXXXXXXXXXXX";
	// Change following detail as per your use case
	private static final String REGION = "ap-south-1";
	private static final String ACCOUNT_ID = "XXXXXXXXXXX";
	private static final String POLICY_JSON = "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Effect\": \"Allow\", \"Action\": [\"iot:Connect\"], \"Resource\": [\"%s\"] },{ \"Effect\": \"Allow\", \"Action\": \"iot:AssumeRoleWithCertificate\", \"Resource\": \"arn:aws:iot:"+REGION+":"+ACCOUNT_ID+":rolealias/"+CREDENTIAL_PROVIDER_RULE_ALIAS+"\" }] }";
	private String certificateARN;

	@Override
	public String handleRequest(RequestClass input, Context context) {
		this.context = context;
		context.getLogger().log("Input: " + input);
		context.getLogger().log("certificateId: " + input.certificateId);
		client = AWSIotClientBuilder.defaultClient();
		String certificateId = input.certificateId;
		certificateARN = "arn:aws:iot:" + REGION + ":" + ACCOUNT_ID + ":cert/" + certificateId;

		String thingName = getThingNameFromCertificate(input.getCertificateId()).replace(" ", "_");
		createThing(thingName);
		context.getLogger().log("Thing created successfully" + thingName);

		String policyName = POLICY_PREFIX + thingName;
		createPolicy(policyName);
		context.getLogger().log("Policy created successfully: " + policyName);

		attachPolicyToCertificate(policyName);
		context.getLogger().log("Policy has been attached to certificate");

		attachThingToCertificate(thingName);
		context.getLogger().log("Thing has been attached to certificate");

		activateCertificate(certificateId);
		context.getLogger().log("Certificate is activated successfully");

		return "Policy and Thing has been created. \nPolicy and thing has been attached to certificate. \nCertificate activation is done.";
	}

	/**
	 * Create Thing
	 * 
	 * @param thingName
	 * @return
	 */
	private CreateThingResult createThing(String thingName) {
		CreateThingRequest createThingRequest = new CreateThingRequest();
		createThingRequest.setThingName(thingName);
		return client.createThing(createThingRequest);
	}

	/**
	 * Create policy
	 * 
	 * @param certificateId
	 */
	private void createPolicy(String policyName) {
		String policyDocument = String.format(POLICY_JSON, certificateARN);
		CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest();
		createPolicyRequest.setPolicyDocument(policyDocument);
		createPolicyRequest.setPolicyName(policyName);
		client.createPolicy(createPolicyRequest);
	}

	/**
	 * attach policy to certificate
	 * 
	 * @param policyName
	 */
	private void attachPolicyToCertificate(String policyName) {
		AttachPolicyRequest attachPolicyRequest = new AttachPolicyRequest();
		attachPolicyRequest.setPolicyName(policyName);
		attachPolicyRequest.setTarget(certificateARN);
		client.attachPolicy(attachPolicyRequest);
	}

	/**
	 * attach thing to certificate
	 * 
	 * @param thingName
	 */
	private void attachThingToCertificate(String thingName) {
		AttachThingPrincipalRequest attachThingPrincipalRequest = new AttachThingPrincipalRequest();
		attachThingPrincipalRequest.setThingName(thingName);
		attachThingPrincipalRequest.setPrincipal(certificateARN);
		client.attachThingPrincipal(attachThingPrincipalRequest);
	}

	/**
	 * update certificate to active
	 * 
	 * @param thingName
	 */
	private void activateCertificate(String certificateId) {
		UpdateCertificateRequest updateCertificateRequest = new UpdateCertificateRequest();
		updateCertificateRequest.setCertificateId(certificateId);
		updateCertificateRequest.setNewStatus(CertificateStatus.ACTIVE);
		client.updateCertificate(updateCertificateRequest);
	}

	/**
	 * get thing name from certificate
	 * 
	 * @param certificateId
	 * @return
	 */
	private String getThingNameFromCertificate(String certificateId) {
		context.getLogger().log((new java.util.Date()).toString());
		DescribeCertificateResult describeCertificate = getCertificateStringFromId(certificateId);
		context.getLogger().log((new java.util.Date()).toString());

		return parseCertificateAndGetCommonName(describeCertificate.getCertificateDescription().getCertificatePem());

	}

	/**
	 * get certificate pem string from certificate id
	 * 
	 * @param certificateId
	 * @return
	 */
	private DescribeCertificateResult getCertificateStringFromId(String certificateId) {
		DescribeCertificateRequest describeCertificateRequest = new DescribeCertificateRequest();
		describeCertificateRequest.setCertificateId(certificateId);
		com.amazonaws.services.iot.model.DescribeCertificateResult describeCertificate = client
				.describeCertificate(describeCertificateRequest);
		return describeCertificate;
	}

	/**
	 * parse certificate and get common name from certificate
	 * 
	 * @param certificateString
	 * @return
	 */
	public String parseCertificateAndGetCommonName(String certificateString) {
		X509Certificate certificate = null;
		CertificateFactory cf = null;
		try {
			if (certificateString != null && !certificateString.trim().isEmpty()) {
				certificateString = certificateString.replace("-----BEGIN CERTIFICATE-----\n", "")
						.replace("-----END CERTIFICATE-----", "").replaceAll("\n", ""); // NEED FOR PEM FORMAT CERT
																						// STRING
				byte[] certificateData = Base64.getDecoder().decode(certificateString);
				cf = CertificateFactory.getInstance("X509");
				certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));
			}
		} catch (CertificateException e) {
			context.getLogger().log(e.getMessage());
		}
		context.getLogger().log("SUBJECT: " + certificate.getSubjectDN().getName());
		context.getLogger().log("final:" + certificate.getSubjectDN().getName().split(",")[0].substring(3));
		return certificate.getSubjectDN().getName().split(",")[0].substring(3);
	}
}
