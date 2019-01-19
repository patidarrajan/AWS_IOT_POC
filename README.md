a# AWS_IOT_POC
AWS Iot Thing Job Creation, new job notification, start job and update the job after downloading firmware through JAVA SDK with UI in JAVAFX | Presigned S3 URL creation

This Application is made for firmware download.

Repository contains 3 projects:
1. Aws-Pre-Signed-Url-Generation: To generate presigned url and use it into job document. 
    NOTE: AWS CLI should be configured

2. Iot-Create-Thing-Job-App: To create iot thing job with UI.
    NOTE: Access key and secret key should be mentioned in aws-iot-sdk-samples.properties
    
3. Iot-Start-Update-Thing-Job-App: To get notification for new job and to start job and then get job document from aws. After getting thing job document, it will download firmware zip from mention url and update the status of job to SUCCEDED or FAILED.
    NOTE: aws-iot-sdk-samples.properties files properties should be mention as per your aws account.

JOB Document: sample-job-document.json

    {
        "operation":"Firmware Update Operation",
        "firmwareUrl":"< Your Firmware Presigned Url which was generated from presigned url generation project >"
    }
    
   
