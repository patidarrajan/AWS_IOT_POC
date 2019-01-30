package com.rajan.listener;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.google.gson.Gson;
import com.rajan.controller.JobController;
import com.rajan.model.JobQueue;

import javafx.application.Platform;
import javafx.scene.paint.Color;

public class NotifyJobListener implements TopicListener {
	private JobController controller;

	public NotifyJobListener(JobController controller) {
		this.controller = controller;
	}

	@Override
	public String onTopicResponse(AWSIotMessage message) {
		// TODO Auto-generated method stub
		System.out.println(message);
		String responseJson = message.getStringPayload();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				JobQueue jobQueue = new Gson().fromJson(responseJson, JobQueue.class);
				if (jobQueue.getJobs() != null) {
					controller.getJobLbl().setText("New Job is available: ");
					controller.getJobLbl().setTextFill(Color.web("#006666"));
					controller.getJobNameLbl().setText(jobQueue.getJobs().getQUEUED().get(0).getJobId());
					controller.getStartJobBtn().setVisible(true);
				}
			}
		});
		return responseJson;
	}

}