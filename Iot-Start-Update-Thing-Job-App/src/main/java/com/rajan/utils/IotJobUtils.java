package com.rajan.utils;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.rajan.utils.PropertyUtil.KeyStorePasswordPair;

/**
 * 
 * @author rajan
 *
 */
public class IotJobUtils {

	private static AWSIotMqttClient AWS_IOT_CLIENT;
	public static final AWSIotQos TestTopicQos = AWSIotQos.QOS0;
	public static final String PROPERTY_FILE_PATH = "app.properties";
	public static final String AWS_THING_TOPIC_URL = "$aws/things/" + PropertyUtil.getConfig("thingName");
	public static final String JOB_NOTIFY_TOPIC = AWS_THING_TOPIC_URL + "/jobs/notify";
	public static final String START_JOB_TOPIC = AWS_THING_TOPIC_URL + "/jobs/start-next";
	public static final String START_JOB_STATUS_TOPIC = AWS_THING_TOPIC_URL + "/jobs/start-next/#";

	static {
		CommandArguments arguments = CommandArguments.parse(new String[0]);
		initClient(arguments);
	}

	public static String updateJobExecutionTopic(String jobId) {
		return AWS_THING_TOPIC_URL + "/jobs/" + jobId + "/update";
	}

	public static String updateJobExecutionStatusTopic(String jobId) {
		return AWS_THING_TOPIC_URL + "/jobs/" + jobId + "/update/#";
	}

	public static void setClient(AWSIotMqttClient client) {
		AWS_IOT_CLIENT = client;
	}

	public static AWSIotMqttClient getAWS_IOT_CLIENT() {
		return AWS_IOT_CLIENT;
	}

	private static void initClient(CommandArguments arguments) {
		String clientEndpoint = arguments.getNotNull("clientEndpoint", PropertyUtil.getConfig("clientEndpoint"));
		String clientId = arguments.getNotNull("clientId", PropertyUtil.getConfig("clientId"));

		String certificateFile = arguments.get("certificateFile", PropertyUtil.getConfig("certificateFile"));
		String privateKeyFile = arguments.get("privateKeyFile", PropertyUtil.getConfig("privateKeyFile"));
		if (AWS_IOT_CLIENT == null && certificateFile != null && privateKeyFile != null) {
			String algorithm = arguments.get("keyAlgorithm", PropertyUtil.getConfig("keyAlgorithm"));

			KeyStorePasswordPair pair = PropertyUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);

			AWS_IOT_CLIENT = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
		}

		if (AWS_IOT_CLIENT == null) {
			String awsAccessKeyId = arguments.get("awsAccessKeyId", PropertyUtil.getConfig("awsAccessKeyId"));
			String awsSecretAccessKey = arguments.get("awsSecretAccessKey", PropertyUtil.getConfig("awsSecretAccessKey"));
			String sessionToken = arguments.get("sessionToken", PropertyUtil.getConfig("sessionToken"));

			if (awsAccessKeyId != null && awsSecretAccessKey != null) {
				AWS_IOT_CLIENT = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
						sessionToken);
			}
		}

		if (AWS_IOT_CLIENT == null) {
			throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
		}
	}

}
