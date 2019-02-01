## AWS IOT JITR Lambda function with Thing & policy creation in JAVA.
This project will provide Just IN Time Registration (JITR) for AWS IOT Devices.

### Project Overview: ###
1. Get certificate details from certificate id.
2. Parse certificate details and get common name from certificate.
3. Creates IOT policy having action of connect. 
4. Creates IOT thing with name from certificate common name.
5. Attach policy and thing to certificate.
6. Activate Certificate.

## Pre-requisites
1. You need registered CA certificate in AWS IOT in same region.
2. Generate device certificate by signed with this CA certificate.
 
