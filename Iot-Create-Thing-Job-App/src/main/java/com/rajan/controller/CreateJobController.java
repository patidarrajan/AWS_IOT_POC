package com.rajan.controller;

import java.util.stream.Collectors;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.model.CreateJobRequest;
import com.amazonaws.services.iot.model.JobExecutionsRolloutConfig;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.iot.model.TimeoutConfig;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.rajan.utils.AwsBuilderUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * 
 * @author rajan
 *
 */
public class CreateJobController {
	private static final String TARGET_SELECTION = "SNAPSHOT";

	@FXML
	private TextField jobName;

	@FXML
	private TextArea jobDescription;

	@FXML
	private ListView<ThingAttribute> thingListView;

	@FXML
	private ListView<String> bucketListView;

	@FXML
	private ListView<String> objectKeyListView;

	@FXML
	private TextField jobTimeout;

	@FXML
	private TextField maxJobRollout;

	@FXML
	private Button createJobBtn;

	@FXML
	private void initialize() throws AWSIotException {
		thingListView.getItems().addAll(AwsBuilderUtils.getInstance().listThing(new ListThingsRequest()));
		thingListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		bucketListView.getItems().addAll(AwsBuilderUtils.getInstance().listS3Buckets());
		bucketListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, final String oldvalue, final String newvalue) {
				loadObjectAsPerBucket(newvalue);
			}
		});
		thingListView.setCellFactory(new ThingCellFactory());
		bucketListView.getSelectionModel().select(0);
		thingListView.getSelectionModel().select(0);

	}

	protected void loadObjectAsPerBucket(String bucket_name) {
		objectKeyListView.getItems().clear();
		objectKeyListView.getItems().addAll(AwsBuilderUtils.getInstance().listS3ObjectsKeyFromBucket(bucket_name));
		objectKeyListView.getSelectionModel().select(0);
	}

	@FXML
	private void createJob(ActionEvent event) {
		CreateJobRequest createJobRequest = new CreateJobRequest();
		createJobRequest.setJobId(jobName.getText());
		createJobRequest.setDescription(jobDescription.getText());
		createJobRequest.setTargets(thingListView.getSelectionModel().getSelectedItems().stream()
				.map(thingAttribute -> thingAttribute.getThingArn()).collect(Collectors.toList()));
		createJobRequest.setTargetSelection(TARGET_SELECTION);

		JobExecutionsRolloutConfig jobExecutionsRolloutConfig = new JobExecutionsRolloutConfig();
		jobExecutionsRolloutConfig.setMaximumPerMinute(Integer.parseInt(maxJobRollout.getText()));
		createJobRequest.setJobExecutionsRolloutConfig(jobExecutionsRolloutConfig);
		TimeoutConfig timeoutConfig = new TimeoutConfig();
		timeoutConfig.setInProgressTimeoutInMinutes(Long.parseLong(jobTimeout.getText()));
		createJobRequest.setTimeoutConfig(timeoutConfig);

		String documentUrl = AwsBuilderUtils.getInstance().getDocumentUrl(
				bucketListView.getSelectionModel().getSelectedItem(),
				objectKeyListView.getSelectionModel().getSelectedItem());
		createJobRequest.setDocumentSource(documentUrl);
		System.out.println(createJobRequest.toString());
		AwsBuilderUtils.getInstance().createJob(createJobRequest);
	}
}

class ThingCell extends ListCell<ThingAttribute> {

	@Override
	protected void updateItem(ThingAttribute item, boolean empty) {
		super.updateItem(item, empty);

		setContentDisplay(ContentDisplay.TEXT_ONLY);
		if (empty) {
			setText(null);
		} else {
			setText(item.getThingName());
		}
	}
}

class ThingCellFactory implements Callback<ListView<ThingAttribute>, ListCell<ThingAttribute>> {

	@Override
	public ListCell<ThingAttribute> call(ListView<ThingAttribute> param) {
		return new ThingCell();
	}
}
