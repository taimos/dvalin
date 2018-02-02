## notification

The notification component provides support for sending e-mails and push messages. The `notification-aws` 
library provides implementations of the `MailSender` using Amazon SimpleEmailService and the `PushService` 
using Amazon SimpleNotificationService.


### E-Mail

Dvalin uses the standard Spring MailSender interface for its email support. The core library provides the 
`TestMailSender` that stores the sent mails into a collection instead of sending them out. This can be 
used in integration tests. The `notification-aws` version uses SES to send emails. The region to use can 
be specified by the property `aws.mailregion`. If it is not set, the region is derived using the strategy 
defined above for the `AWSClient` annotation.

### Push Notifications

To use the AWS push implementation provide configurations containing the ARN of the platform application in SNS.

* `aws.push.applicationarn.gcm` - the ARN of the GCM application 
* `aws.push.applicationarn.apns` - the ARN of the APNS application 
* `aws.push.applicationarn.apns.sandbox` - the ARN of the APNS_SANDBOX application 

