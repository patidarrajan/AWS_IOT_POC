package com.rajan.listener;

import java.io.IOException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.google.gson.Gson;
import com.rajan.controller.JobController;
import com.rajan.model.Execution;
import com.rajan.model.JobQueue;
import com.rajan.utils.JobOperations;
import com.rajan.utils.IotJobUtils;

import javafx.application.Platform;

public class StartJobListener implements TopicListener {
	private JobController controller;

	public StartJobListener(JobController controller) {
		this.controller = controller;
	}

	@Override
	public String onTopicResponse(AWSIotMessage message) {
		String responseJson = message.getStringPayload();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				JobQueue jobQueue = new Gson().fromJson(responseJson, JobQueue.class);
				Execution jobExecution = jobQueue.getExecution();
				try {
					doJobOperationAndUpdateStatus(jobExecution, controller);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return message.getStringPayload();
	}

	private void startChangeCertificateUIChange(Execution jobExecution) {
		controller.getJobLbl().setText(jobExecution.getJobId() + " Job Status: " + jobExecution.getStatus());
		controller.getTimeoutLbl().setText("Timeout: " + jobExecution.getApproximateSecondsBeforeTimedOut() + " sec");
		controller.getJobNameLbl().setText("");
		controller.getTimeoutLbl().setVisible(true);
		controller.getStartJobBtn().setVisible(false);
		controller.getJobOperationHbox().setVisible(true);
		controller.getOperationLbl().setText(jobExecution.getJobDocument().getOperation());
		System.out.println(jobExecution.getJobDocument().getFirmwareUrl());
		controller.getDownloadStatusHbox().setVisible(true);
		controller.getDownloadStatusLbl().setText("Replacing old certificate with new one...");
	}

	private void startDownloadUIChange(Execution jobExecution) {
		controller.getJobLbl().setText(jobExecution.getJobId() + " Job Status: " + jobExecution.getStatus());
		controller.getTimeoutLbl().setText("Timeout: " + jobExecution.getApproximateSecondsBeforeTimedOut() + " sec");
		controller.getJobNameLbl().setText("");
		controller.getTimeoutLbl().setVisible(true);
		controller.getStartJobBtn().setVisible(false);
		controller.getJobOperationHbox().setVisible(true);
		controller.getOperationLbl().setText(jobExecution.getJobDocument().getOperation());
		System.out.println(jobExecution.getJobDocument().getFirmwareUrl());
		controller.getDownloadStatusHbox().setVisible(true);
		controller.getDownloadStatusLbl().setText("Downloading...");
	}

	/**
	 * do job operation
	 * 
	 * @param jobExecution
	 * @param jobController 
	 * @throws IOException
	 */
	private void doJobOperationAndUpdateStatus(Execution jobExecution, JobController jobController) throws IOException {

		String operation = jobExecution.getJobDocument().getOperation();
		switch (operation.toLowerCase()) {
		case "change_certificate":
			startChangeCertificateUIChange(jobExecution);
			JobOperations.changeCertificate(jobExecution.getJobDocument());
			updateJobAsCompleted(jobExecution.getJobId(), operation);
			break;
		case "firmware update operation":
			startDownloadUIChange(jobExecution);
			String firmwareUrl = jobExecution.getJobDocument().getFirmwareUrl();
			JobOperations.downloadFromUrl(firmwareUrl, "firmware_update.zip");
			updateJobAsCompleted(jobExecution.getJobId(), operation);
			break;
		default:
			updateJobAsFailed(jobExecution.getJobId(), operation);
			System.err.println("JOB Failed");
			break;
		}
	}

	private void updateJobAsCompleted(String jobId, String operation) {
		String payload = "{ \"status\": \"SUCCEEDED\"}";
		try {
			IotJobUtils.getAWS_IOT_CLIENT().publish(IotJobUtils.updateJobExecutionTopic(jobId), payload);
		} catch (AWSIotException e) {
			System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
		}
		controller.getDownloadStatusLbl().setText(operation + " : Success");
		controller.getJobLbl().setText(jobId + " Job Status: SUCCEEDED");

	}

	private void updateJobAsFailed(String jobId, String operation) {
		String payload = "{ \"status\": \"FAILED\"}";
		try {
			IotJobUtils.getAWS_IOT_CLIENT().publish(IotJobUtils.updateJobExecutionTopic(jobId), payload);
		} catch (AWSIotException e) {
			System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
		}
		controller.getDownloadStatusLbl().setText(operation + " : Success");
		controller.getJobLbl().setText(jobId + " Job Status: FAILED");

	}

}
