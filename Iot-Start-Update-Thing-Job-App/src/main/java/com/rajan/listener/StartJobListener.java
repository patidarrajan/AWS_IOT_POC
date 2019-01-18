package com.rajan.listener;

import java.io.IOException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.google.gson.Gson;
import com.rajan.controller.JobController;
import com.rajan.model.Execution;
import com.rajan.model.JobQueue;
import com.rajan.utils.DownloadUtils;
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
				startDownloadUIChange(jobExecution);
				downloadFirmwareAndUpdateStatus(jobExecution);
				disconnect();
			}
		});
		return message.getStringPayload();
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

	private void downloadFirmwareAndUpdateStatus(Execution jobExecution) {
		try {
			DownloadUtils.downloadFromUrl(jobExecution.getJobDocument().getFirmwareUrl(), "firmware_update.zip");
			updateJobAsCompleted(jobExecution.getJobId());
		} catch (IOException e) {
			updateJobAsFailed(jobExecution.getJobId());
			System.err.println("JOB Failed");
			e.printStackTrace();
		}
	}

	private void disconnect() {
		try {
			IotJobUtils.getAWS_IOT_CLIENT().disconnect();
		} catch (AWSIotException e) {
			e.printStackTrace();
		}
	}

	private void updateJobAsCompleted(String jobId) {
		String payload = "{ \"status\": \"SUCCEEDED\"}";
		try {
			IotJobUtils.getAWS_IOT_CLIENT().publish(IotJobUtils.updateJobExecutionTopic(jobId), payload);
		} catch (AWSIotException e) {
			System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
		}
		controller.getDownloadStatusLbl().setText("Success");
		controller.getJobLbl().setText(jobId + " Job Status: SUCCEEDED");

	}

	private void updateJobAsFailed(String jobId) {
		String payload = "{ \"status\": \"FAILED\"}";
		try {
			IotJobUtils.getAWS_IOT_CLIENT().publish(IotJobUtils.updateJobExecutionTopic(jobId), payload);
		} catch (AWSIotException e) {
			System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
		}
		controller.getDownloadStatusLbl().setText("Failed");
		controller.getJobLbl().setText(jobId + " Job Status: FAILED");

	}

}
