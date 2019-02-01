## AWS_IOT_POC
AWS Iot Thing Job Creation, new job notification, start job and update the job after downloading firmware through JAVA SDK with UI in JAVAFX | Presigned S3 URL creation

This Application is made for firmware download.

### **Repository contains 3 projects:**
1. [Aws-Pre-Signed-Url-Generation:](https://github.com/patidarrajan/AWS_IOT_POC/tree/master/Aws-Pre-Signed-Url-Generation) To generate presigned url and use it into job document. 
    NOTE: AWS CLI should be configured

2. [Iot-Create-Thing-Job-App:](https://github.com/patidarrajan/AWS_IOT_POC/tree/master/Iot-Create-Thing-Job-App) To create iot thing job with UI.
    NOTE: Access key and secret key should be mentioned in aws-iot-sdk-samples.properties
    
3. [Iot-Start-Update-Thing-Job-App:](https://github.com/patidarrajan/AWS_IOT_POC/tree/master/Iot-Start-Update-Thing-Job-App) To get notification for new job and to start job and then get job document from aws. After getting thing job document, it will download firmware zip from mention url and update the status of job to SUCCEDED or FAILED.
    NOTE: aws-iot-sdk-samples.properties files properties should be mention as per your aws account.

#### **JOB Document:**  sample-job-document.json

    {
        "operation":"Firmware Update Operation",
        "firmwareUrl":"< Your Firmware Presigned Url which was generated from presigned url generation project >"
    }
    
**Note:** Place This job document file into S3. Attach this job document when creating new IOT job. 

### **Snapshots:**
#### 1. Create Job 
![Create Job](https://github.com/patidarrajan/AWS_IOT_POC/blob/master/Iot-Create-Thing-Job-App/src/main/resources/img_CreateJob.png)

#### 2. Job Notification after Creation
![Job Notification after Creation image](https://github.com/patidarrajan/AWS_IOT_POC/blob/master/Iot-Start-Update-Thing-Job-App/src/main/resources/img_NotifyJob.png)


#### 3. Start Job to download firmware
![Start Job to download firmware image](https://github.com/patidarrajan/AWS_IOT_POC/blob/master/Iot-Start-Update-Thing-Job-App/src/main/resources/img_StartJob.png)


#### 4. Download Firmware and update status of job to Success or Failed as per download status.
![Download Firmware and update status of job image](https://github.com/patidarrajan/AWS_IOT_POC/blob/master/Iot-Start-Update-Thing-Job-App/src/main/resources/img_DownloadFirmware.png)
