[![Build Status](https://travis-ci.org/taimos/dvalin.svg)](https://travis-ci.org/taimos/dvalin)
[![codecov.io](https://codecov.io/github/taimos/dvalin/coverage.svg?branch=master)](https://codecov.io/github/taimos/dvalin?branch=master)

# dvalin - Taimos Microservice Framework

Dvalin is a Java micro service framework based on several open-source frameworks to combine the best tools into one quick start suite for fast, reliable and scaling micro services.
The core technology is the Spring framework and dvalin uses our Daemon Framework as the lifecycle management for the service process.

To use dvalin in your project add the maven dependencies as shown below. 
It is recommended to set the dvalin version as property to make sure all modules you use have the same version.

```
<dependency>
    <groupId>de.taimos</groupId>
    <artifactId>dvalin-MODULE</artifactId>
    <version>${dvalin.version}</version>
</dependency>
```

Then add dvalin as a BOM to the maven dependency management to ensure compatibly versions of used third-party libraries.

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>de.taimos</groupId>
            <artifactId>dvalin-parent</artifactId>
            <version>${dvalin.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
 
# Parts of dvalin
 
Dvalin provides several independent but combineable libraries serving different purposes around the runtime and 
interaction of micro services. By adding a library as a Maven dependency, it gets activated automatically and 
all the needed Spring components are started.

These libraries are:

* `daemon` - the core library for lifecycle and basic Spring enhancements
* `jaxrs`- implement JAX-RS based REST services using the Apache CXF framework
* `jpa` - connect to SQL databases using the popular Hibernate framework
* `mongodb` - connect to MongoDB document store
* `dynamodb`- connect to AWS DynamoDB data storage
* `cloud` - basic tools to communicate with Cloud providers
* `cluster` - basic tools to form a cluster
* `template` - templating functionality
* `notification` - notification service to send e-mails and use template engines
* `monitoring` - monitoring service to report statistics of your service
* `interconnect` - communication framework to connect micro services with each other
* `orchestration` - orchestration tools like service discovery and global configuration
* `test` - utilities for writing tests

## daemon

The `daemon` part includes the Taimos daemon framework into dvalin. You can use the following conditional annotations from the daemon framework:

* `@BeanAvailable` - Only create the annotated bean if the denoted bean is also available
* `@OnSystemProperty` - Only create the annotated bean if the denoted system property is set and if it optionally also has the given value

The entry point for your application is the `DvalinLifecycleAdapter`. Just extend it and implement a main method that calls the static `start` method. 
This configures your application to read properties from a file called `dvalin.properties`.

By overriding the `setupLogging()` Method you can enable the `StructuredLogConfigurer` instead of the default Log4j configuration. 
Logging will the use the console and print all entries in JSON format. The `DvalinLogger` can be used to modify the MDC for a single log line.

## jaxrs

The `jaxrs` library adds tooling to implement JAX-RS web services. It combines the powers of the Spring framework, 
the Apache CXF framework, the Jackson JSON mapper and the Eclipse Jetty web server. To include it add the maven dependency to your POM file.
It will then automatically configure a Jetty web server running on a given port and start a CXF JAX-RS handler into the running Jetty server. 
By default all beans annotated with `@JaxRsComponent` will be registered as provider or endpoint respectively. 
Additionally several extra features are implemented.

You can inject the `DvalinRSContext` to get easier access to some of the features.  

### Monitoring

For every request an instance of `de.taimos.dvalin.jaxrs.monitoring.InvocationInstance` will be registered into the MessageContext. 
It will contain a unique request id and will measure the runtime of the request for monitoring purpose.

### Providers

You can activate preconfigured JacksonProvider or WebExceptionMapper by subclassing the respective type and annotate it with `@JaxRsComponent`.

### Remote service

By annotating a private field of a JAX-RS client interface with `@RemoteService` dvalin will automatically create a JAX-RS client 
proxy and then inject it like any other autowired bean into the desired location. You can provide the base URL of the service 
or use defined system properties to resolve the URL of the service.

### Swagger

The JAX-RS web service it automtically documented using the Swagger specification and published as `swagger.{json|yaml}` on the
root of your API URL. Also a graphical Swagger ui is served under `/swagger-ui`.

### WebSocket support

You can publish a WebSocket just by annotating any class with `@WebSocket`. If you like you can use the 
abstract `ServerJSONWebSocketAdapter` as a base class. For the client side (e.g. in tests) you can use the `ClientSocketAdapter` 
to connect the the published socket.

### Security

There are several helper classes and beans to provide identity and access management functionality. 

#### Authentication

You can subclass the abstract `AuthorizationProvider` and return SecurityContexts in the methods to 
implement or you can provide implementations of predefined bean interfaces to activate ready-to-use 
authentication filters for BasicAuth or token based authentication.

These interfaces are the `IBasicAuthUserDAO` or the `ITokenAuthUserDAO`. If you put an implementation 
in your Spring context the JAX-RS filter will be deployed using the `@BeanAvailable` annotation of the core library.

#### Authorization

To limit access to sensitive resources you can use the injectable `DvalinRSContext` that provides methods to retrieve 
information about the logged in user or to assert given roles. Additionally dvalin registers filters that 
support the following annotations to limit access to resources.

* `@RolesAllowed` - only users having at least one of the provided roles are granted access
* `@LoggedIn` - only request that contain a valid logged in user are processed

To help in protecting user credentials the class `HashedPassword` implements everything needed to store 
password hashes secured by the SHA-512 function using a 512 bit salt and a dynamic number of iterations.

### Configuration

Several settings of the dvalin framework can be customized using system properties which are described 
in `de.taimos.dvalin.jaxrs.SpringCXFProperties`.

### Testing

moved to `test` sub-project.

### JSON Web Tokens

For Web Token support include the additional dependency `dvalin-jaxrs-jwtauth` and set the following properties:

* `jwtauth.issuer` - the issuer of the tokens
* `jwtauth.secret` - the shared secret to sign web tokens with
* `jwtauth.timeout` - optional timeout of the tokens (defaults to one hour)

You can then create WebTokens using the `JWTAuth` bean and they are automatically validated when set as Bearer type Authorization.

## jpa

The `jpa` library adds JPA and Hibernate support including changeset management using liquibase. By setting some 
system properties you get the full support to store and retrieve data from relational database systems. 
Supported databases are currently HSQL, PostgreSQL and MySQL. You have to add the desired jdbc driver to your classpath manually.

The following settings are possible:

* `ds.type` - {MYSQL|POSTGRESQL|HSQL} type of the database
* `ds.package` - root package of your entities (path notation with /)
* `ds.showsql` - {true|false} to log all SQL statements to the logger
* `ds.demodata` - {true|false} to insert data from the file `sql/demodata_${ds.type}.sql` on startup

For MySQL the following extra setting are possible:

* `ds.mysql.host` - the hostname of the database server
* `ds.mysql.port` - the port number of the database server
* `ds.mysql.db` - the name of the database
* `ds.mysql.user` - the user name of the database server
* `ds.mysql.password` - the password of the database server

For PostgreSQL the following extra setting are possible:

* `ds.pgsql.host` - the hostname of the database server
* `ds.pgsql.port` - the port number of the database server
* `ds.pgsql.db` - the name of the database
* `ds.pgsql.user` - the user name of the database server
* `ds.pgsql.password` - the password of the database server

The library also provides a general purpose DAO interface (`IEntityDAO`) and an abstract implementation
(`EntityDAOHibernate`) with many helper methods to ease the development of the data layer. For this to 
work your entities have to implement the `IEntity` interface.

If you use the JodaTime library you can annotate Date members with the `JodaDateTimeType` to activate 
JodaTime support for JPA.

All changesets contained or referenced in the file `liquibase/changelog.xml` are checked and applied 
on startup by the liquibase database migration library.

## mongodb

The mongodb library adds support for the MongoDB document store. By adding the dependency you get the 
full support to interact with MongoDB databases including an in-memory database for tests. 

### Connection properties

The following settings are possible:

* `mongodb.type` - {fake|real} connect to real MongoDB database or in-memory version using `Fongo`
* `mongodb.name` - the name of the database to use for data storage
* `mongobee.enabled` - {true|false} use mongobee for database migration
* `mongobee.basePackage` - the base package of the Mongobee changesets
* `mongodb.demodata` - {true|false} load demodata on startup from ND-JSON files

For connections to real MongoDB databases, these extra properties can be set:

* `mongodb.host` - the host of the MongoDB instance (default: localhost)
* `mongodb.port` - the port of the MongoDB instance (default: 27017)
* `mongodb.uri` - instead of host and port you can specify the complete connection string
* `mongodb.socketTimeout` - the socket timeout of the connection (default: 10 seconds)
* `mongodb.connectTimeout` - the connection timeout of the connection attempt (default: 10 seconds)


### Abstract entity and DAO interface

The library provides a general purpose DAO interface (`ICrudDAO`) and an abstract implementation
(`AbstractMongoDAO`) with many helper methods to ease the development of the data layer. For this to 
work your entities have to extend the `AEntity` superclass. The DAOs created have integrated support 
for JodaTime classes. If you want to use polymorphic types in your entities make sure to implement 
`@IMappedSupertype` on the super class. This advises the Jackson mapper to include type information 
into the created JSON for deserialization. 

### Changesets

For database migration purpose the mongobee library is included and is configured as denoted above 
using system properties. See the mongobee documentation on how to implement changesets. 
For Index creation take a look at the `ChangelogUtil` helper class.

### MongoDBInit

To prefill the database with startup data or test data for integration tests put file on your classpath 
into the package `mongodb` and name them using the following pattern: `<CollectionName>.ndjson`
If you set the system property `mongodb.demodata`to `true` dvalin will populate the given collections 
with the objects contained in this new-line delimited files. Just put one JSON object per line. 

### DocumentLinks

Another feature of dvalin's MongoDB support are DocumentLinks. These allow for references between your 
documents. To include a reference in one of your entities just add a field of the generic type 
`DocumentLink` and let your referenced entity extend `AReferenceableEntity` instead of `AEntity`. 
Dvalin will then include a reference to the given document in your JSON which you can resolve 
by injecting the `IDlinkDAO` wherever you want.

## dynamodb

The dynamodb library adds support for the AWS DynamoDB data store. By including the AWS cloud module you 
get the full support to interact with DynamoDB using the SDK or the DynamoDBMapper.

### Abstract DAO implementation

The library provides a general purpose DAO implementation (`AbstractDynamoDAO`) with automatic initialization
and table creation. If you set the `dynamodb.url` property the endpoint of the SDK is reconfigured. This 
enables the use of the local DynamoDB version for development.

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

## cluster

The `cluster` libraries provide tools to form a cluster of services. Currently only Hazelcast is available 
under `cluster-hazelcast` and can be added using maven. It adds hazelcast to the classpath and auto configuration for clusters.
To form a cluster set the system property `hazelcast.cluster` to `true` and implement `ClusterInfoProvider` as a component.
To connect to a cluster implement the same interface and set the property `hazelcast.client` to the name 
of a cluster that the provider can resolve.

## templating

This component provides a templating engine based on Velocity and PDF generation based on XDocReport or JasperReports.

### Template engine

For templating the Velocity template engine is used. Inject the `ITemplateResolver` in your bean to process 
templates. You can provide a location relative to the folder `/velocity` in your classpath or you provide 
the template as String.

### PDF generation (XDocReport)

For PDF generation XDocReport can be used. Inject the `ReportService` to create PDF files. You provide a location 
relative to the folder `/xdocreport` in your classpath targeting a docx file. This file is then merged 
with the given context and a PDF is created. 

### PDF generation (JasperReports)

For PDF generation JasperReports can be used. Inject the `ReportService` to create PDF files. You provide a location 
relative to the folder `/reports` in your classpath targeting a jrxml file. This file is then merged 
with the given context and a PDF is created. 

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

* `aws.pushApplicationARN.GCM` - the ARN of the GCM application 
* `aws.pushApplicationARN.APNS` - the ARN of the APNS application 
* `aws.pushApplicationARN.APNS_SANDBOX` - the ARN of the APNS_SANDBOX application 

## monitoring

The monitoring service allows sending statistics to different backends to collect metering data.

### Backends

Currently a logging backend and AWS CloudWatch are supported. To enable the backend put the desired 
library on the classpath. Only one backend can be on the classpath simultaneously.

### Usage

To send metrics manually inject the `MetricSender` interface and call the `sendMetric` method. 
You have to supply some coordinates for the metric and the value itself.

Dvalin also provides AspectJ annotations that send metrics automatically.

* `@ExecutionTime` - method annotation that reports the execution time of the method

## interconnect

*coming soon*

## orchestration

The `orchestration` libraries provide tools for orchestration like service discovery and global configuration.
Currently only etcd is available under `orchestration-etcd` and can be added using maven. 
To use it set the system property `orchestration.etcd.peers` to a comma separated list of peer URIs.
You can then autowire instances of `ServiceDiscovery` and `GlobalConfiguration`.

Other bindings are planned for the future.


## test

The `test` library provides utilities to help in writing test for dvalin based projects. It adds dependencies
to Mockito, JUnit and Concordion.

### Base class for Mockito unit tests

The class `AbstractMockitoTest` configures JUnit to use the Mockito runner and configures the log4j framework
before test execution.

### InjectionUtils

The `InjectionUtils` helper class provides methods to inject dependencies into beans. This allows to fill 
`@Autowired` annotated fields in unit tests with mocked objects. See the javadoc of the class for further 
information about the features.

### Asserting exceptions

By using the `AssertErrors` util you can assert that your code throws given exceptions using lambdas.

### JAX-RS

For integration tests of your JAX-RS service there are several helpers implemented in Dvalin. There 
is an abstract `APITest` class that provides several helper methods to start web requests, open web sockets
and assert responses from web service endpoints. To assert conditions across multiple threads you can use 
the AsyncAssert class. To inject client proxies to your API under test just annotate a field in your test 
class with `@TestProxy` and dvalin will create the desired proxy and link it to the server instance during test execution.  

The class `AbstractJaxRsPowermockTest` configures JUnit to use the PowerMockRunner runner, configures the log4j framework
before test execution and prepare the JAXRS context for mocking.

The `AnnotationAssert` utils allows testing `@JaxRsComponents` for annotations like `@LoggedIn`.
 
The `ContextMockUtil` helps mocking the JAXRS context. 
