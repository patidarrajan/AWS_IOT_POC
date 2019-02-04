## AWS IOT JITR with Thing and Policy creation using JAVA.
This POC will provide **Just In Time Registration (JITR) of custom certificate and Thing creation with connect policy** for AWS IOT Devices. You just need to add name of thing in common name while creation of device certificate and thing will be created with attached policy & certificate and common name as thing name.

### Project Overview: ###
1. Get certificate details from certificate id.
2. Parse certificate details and get common name from certificate.
3. Creates IOT policy having action of connect. 
4. Creates IOT thing with name from certificate common name.
5. Attach policy and thing to certificate.
6. Activate Certificate.
7. Now your device can connect to AWS using this custom certificate.

## Step for JITR & Thing creation
### Create CA Certificate:
1. openssl genrsa -out CACertificate.key 2048

2. openssl req -x509 -new -nodes -key CACertificate.key -sha256 -days 365 -out CACertificate.pem
   - Enter necessary details like city, country, etc.

### Create private key verification certificate using CA certificate
3. aws iot get-registration-code
   - This registration code will be used in following step

4. openssl genrsa -out privateKeyVerification.key 2048

5. openssl req -new -key privateKeyVerification.key -out privateKeyVerification.csr
   - Enter necessary details. **Note:** In `common name`, Enter `registration code` which copied before.

6. openssl x509 -req -in privateKeyVerification.csr -CA CACertificate.pem -CAkey CACertificate.key -CAcreateserial -out privateKeyVerification.crt -days 365 -sha256
   - After this step, CA certificate will create. Now We need to register it to AWS.

### Register & Activate Certificate to AWS
7. aws iot register-ca-certificate --ca-certificate file://CACertificate.pem --verification-certificate file://privateKeyVerification.crt
   - This will output like: 
	{
	    "certificateArn": "< certificateArn >",
	    "certificateId": "< certificateId >"
	}

8. certId=< output certificateId >
	- Assign certificateId to certId variable.

9. aws iot describe-ca-certificate --certificate-id $certId
	- It will give output with certificate details.

10. aws iot update-ca-certificate --certificate-id $certId --new-status ACTIVE
	- It will activate CA certificate. You can do it from AWS console also.

11. aws iot update-ca-certificate --certificate-id  $certId --new-auto-registration-status ENABLE
	- It will enable auto registration of certificate.

### Device Certificate Registration
12. To verify certificate registration request subscribe to following topic from AWS IOT core Test.
	- $aws/events/certificates/registered/#
	- you can skip this step.

13. openssl genrsa -out device.key 2048
14. openssl req -new -key device.key -out device.csr
15. openssl x509 -req -in device.csr -CA CACertificate.pem -CAkey CACertificate.key -CAcreateserial -out device.crt -days 365 -sha256
16. cat device.crt CACertificate.pem > deviceAndCACert.crt

### create AWS lamda function and IAM role.
- create AWS lambda function with JAVA 8 and with IAM role which having IOT Full Access policy. Create JAR from this project using `mvn clean install` and upload JAR to Lambda.   
- When new device will send request with new certificate which signed with registered CA cerificate then it will send MQTT message to $aws/events/certificates/registered/# topic.
- We need to create IOT rule from Act section to trigger lambda function when new certification registered message will trigger. 
	- Add rule query statement `SELECT * FROM '$aws/events/certificates/registered/#'` and action as `Send message to Lambda function` by selecting created Lambda function.

### Test
17. mosquitto_pub --cafile root.cert --cert deviceAndCACert.crt --key device.key -h <aws-endpoint> -p 8883 -q 1 -t  foo/bar -i  anyclientID --tls-version tlsv1.2 -m "RegisterCertificateAndCreateThingAndPolicy" -d
	- Note: you need mosquito client to use above command. You can get endpoint by: aws iot describe-endpoint. you can get root.cert from [here](https://www.amazontrust.com/repository/AmazonRootCA1.pem). 
	- Now you can verify that new thing is created. You will also see that this thing and policy attached to new certificate. Cretificate willbe marked as active.
	- I added policy with just only for connect action. You can change policy as per your requirement like publish, subscribe, etc.

- [Reference](https://aws.amazon.com/blogs/iot/just-in-time-registration-of-device-certificates-on-aws-iot/)

