package com.rajan.controller;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.rajan.listener.NotifyJobListener;
import com.rajan.listener.StartJobListener;
import com.rajan.listener.TestTopicListener;
import com.rajan.utils.IotJobUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * 
 * @author rajan
 *
 */
public class JobController {
	@FXML
	private Label jobLbl;

	@FXML
	private HBox jobOperationHbox;

	@FXML
	private Label operationLbl;

	@FXML
	private HBox downloadStatusHbox;

	@FXML
	private Button startJobBtn;

	@FXML
	private Label downloadStatusLbl;

	@FXML
	private Label timeoutLbl;

	@FXML
	private Label jobNameLbl;

	@FXML
	private void initialize() throws AWSIotException {
		IotJobUtils.getAWS_IOT_CLIENT().connect();
		AWSIotTopic topic = new TestTopicListener(IotJobUtils.JOB_NOTIFY_TOPIC, IotJobUtils.TestTopicQos,
				new NotifyJobListener(this));
		IotJobUtils.getAWS_IOT_CLIENT().subscribe(topic, true);

	}

	@FXML
	private void handleButtonAction(ActionEvent event) {
		String payload = "{ \"clientToken\": \"token\"}";
		try {
			AWSIotTopic topic = new TestTopicListener(IotJobUtils.START_JOB_STATUS_TOPIC, IotJobUtils.TestTopicQos,
					new StartJobListener(this));
			IotJobUtils.getAWS_IOT_CLIENT().subscribe(topic, true);

			IotJobUtils.getAWS_IOT_CLIENT().publish(IotJobUtils.START_JOB_TOPIC, payload);
		} catch (AWSIotException e) {
			System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
		}
	}

	public Label getJobLbl() {
		return jobLbl;
	}

	public Label getTimeoutLbl() {
		return timeoutLbl;
	}

	public Button getStartJobBtn() {
		return startJobBtn;
	}

	public HBox getJobOperationHbox() {
		return jobOperationHbox;
	}

	public Label getOperationLbl() {
		return operationLbl;
	}

	public HBox getDownloadStatusHbox() {
		return downloadStatusHbox;
	}

	public Label getDownloadStatusLbl() {
		return downloadStatusLbl;
	}

	public Label getJobNameLbl() {
		return jobNameLbl;
	}

}
