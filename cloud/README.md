## cloud

The `cloud` libraries provide SDKs for cloud service providers. Currently only Amazon Web Services 
is available under `cloud-aws` and can be added using maven. It provides the core dependency to the 
Java AWS SDK and the annotation `@AWSClient` to inject clients to access the AWS API. Just annotate 
a member extending `AmazonWebServiceClient` and dvalin will automatically inject a configured instance into your bean.

Region selection occurs as follow:

* If present the `region` value of the annotation is evaluated as Spring expression
* If present the property `aws.region` is used
* If present the environment variable `AWS_DEFAULT_REGION` is used
* If present the environment variable `AWS_REGION` is used
* If running on an EC2 instance the current region is used
* The SDK's default region is used

If `aws.accessKeyId` and `aws.secretKey` are present as properties they will be used to sign the requests
to the AWS API. Otherwise the following chain will be used:

* Use environment variables
* Use system properties
* Use profile information
* Use EC2 instance profile

### Utility beans

There are two utility beans that implement common use cases in EC2 and CloudFormation. 
See `EC2Context` and `CloudFormation` beans for details.

In addition you can let Dvalin signal the current CloudFormation stack by setting 
the property `aws.cfnsignal` to `true`.

### ParameterStore

To use the SimpleSystemsManager Parameter Store as configuration source, set the `ParameterStorePropertyProvider` in your main class.
It will list all visible parameters and use the last part of the name (without the path) as a key.
To support local configuration, environment variables override values from SSM. 

You have to add the optional module `dvalin-cloud-aws-ssm`
