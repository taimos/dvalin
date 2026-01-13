# current master
* Update dependencies
    * ActiveMQ 5.16.8
    * Apache CXF 3.5.11
    * AWS 1.12.793
    * Bouncycastle Provider 1.81
    * Guava 33.4.2-jre
    * Jetty 9.4.58.v20250814
    * Joda-Time 2.14.0
    * JSON Small and Fast Parser 2.5.2
    * Junit 5.11.4
    * Liquibase 4.32.0
    * Log4J 2.25.3
    * Mongo Java Driver 4.11.5
    * Mongock 5.5.1
    * Nimbus JOSE+JWT 10.3
    * Swagger 2.2.40
    * Velocity Engine 2.4.1
* Bugfix: Fixing DaemonExceptionMapper
* Bugfix: Serialize EventSender messages to json
* Bugfix: Fixes problem with events time to live set to 10 seconds
* Bugfix: Fixing problem with retry messages in Interconnect
* Bugfix: Better error handling in CloudconductorPropertyProvider
* Bugfix: Throw of timeout exceptions for retries instead of DaemonError, restore 1.35 behavior 
* Bugfix: Fallback for old dates (before 1970) for mongo
* Fixed vulnerabilities: CVE-2024-13009(Jetty), CVE-2025-23184(Apache CXF), CVE-2024-57699 (Json-smart),CVE-2025-27533 (ActiveMQ),CVE-2025-68161(Log4j)
* Logging improvement and extension options for DaemonMessageListener
* Add TLS server parameters for JAX-RS
* Improved Errormessage in case of non parseable JSON strings in the space of InterconnectObjects and Messsaging.
* Add connection pool settings and idle timeout to MongoDB client configuration


# 1.37
* Major bug in interconnect core: DaemonScanner causes IllegalArgumentException due to wrong path of TimeoutException

# 1.36
* Update dependencies
    * Spring 5.3.39
    * AWS 1.12.772
    * Jetty 9.4.56.v20240826
    * Joda-Time 2.12.7
    * Log4J 2.23.1
    * ActiveMQ 5.16.7
    * Apache CXF 3.5.9
    * JSON Small and Fast Parser 2.5.1
    * Jackson 2.17.2
    * Guava 33.3.0-jre
    * Hazelcast 5.3.7
    * Swagger 2.2.22
    * Liquibase 4.28.0
    * Mongo Java Driver 4.11.4
    * Mongock 5.4.4
    * Bouncycastle Provider 1.78.1
    * Nimbus JOSE+JWT 9.41
    * Commons Codec 1.17.0
    * Junit 5.10.3
    * Bson 2.15.1
    * Concordion 4.0.1
* Removed (unused) cglib from  dvalin-jaxrs
* Removed concordion-extensions
* Migrated all JUnit tests to Junit 5 syntax
* Removed mongodb-driver-legacy from mongodb
    * Deprecated mongo functionality was removed
    * Complete overhaul for entity handling
    * The old functionality is still available with the mongodb-legacy library
* Breaking: Removed Junit 5 Vintage engine and Junit 4 (can be added in projects that need it)
* Fixed vulnerabilities: CVE-2023-52428(nimbus-jose-jwt), CVE-2024-29857,CVE-2024-30171,CVE-2024-30172,CVE-2024-34447 (bouncycastle), CVE-2024-28752,CVE-2024-29736 (Apache CXF), CVE-2024-38808 (Spring Framework)
* Corrected the use of @Nullable and @Nonnull annotations on created ivos and events, especially on the generated builders 
* Add support for h2 embedded database
* Fix conflicting jetty ContextHandlers for static files and web frontend
    
# 1.35
* Update dependencies
    * Spring 5.3.31
    * AWS 1.12.641
    * Jetty 9.4.53.v20231009
    * Log4J 2.22.0
    * ActiveMQ 5.16.7
    * Apache CXF 3.5.7
    * JSON Small and Fast Parser 2.5.0
    * Guava 32.1.3-jre
    * Hazelcast 5.3.6
    * Swagger 2.2.19
    * Liquibase 4.25.0
    * Mongo Java Driver 4.11.1
    * Mongock 5.3.5
    * Mongo Java Server 1.43.0
    * Mockito 4.11.0
    * Bouncycastle Provider 1.74
    * Maven core and maven plugins
* Added support for additional configuration files when using SpringDaemonTestRunner
* Migrated from JUnit 4 to JUnit 5
    * Drop support for Powermock
    * Switch Mockito to Mockito Inline for usage in static mocks
* Fixing an recurring ActiveMQ problem where messages can't be sent because ActiveMQ is to slow opening destination. 
  We do a resend after configurable retry time. If problem does not occur, nothing hanges.
* IVO generator: replaced old Apache BeanUtils with Spring beans BeanUtils
* Better error handling for DaemonStarter.abortSystem
* Interconnect: Check topic name for null values
* Fixed vulnerabilities: CVE-2023-33201(Bouncycastle),CVE-2023-40167(Jetty),CVE-2023-45860(Hazelcast),CVE-2023-46604(ActiveMQ),CVE-2024-21634(AWS)

# 1.34
* Update dependencies
    * Spring 5.3.27
    * AWS 1.12.467
    * Jetty 9.4.51.v20230217
    * Apache CXF 3.5.6
    * JSON Small and Fast Parser 2.4.9
    * Jackson 2.15.2
    * Guava 32.0.0
    * Hazelcast 5.3.0
    * Swagger 2.2.10
    * Liquibase 4.21.1
* Fixed vulnerabilities: CVE-2023-26048, CVE-2023-26049, CVE-2023-1370, CVE-2023-20861, CVE-2023-20863, CVE-2023-1370, 
CVE-2022-40152, CVE-2022-46364, CVE-2022-46363, CVE-2023-2976, CVE-2020-8908, CVE-2022-1471, CVE-2023-33264

# 1.33
* Update dependencies
    * Spring 5.3.23
    * AWS 1.12.344
    * Jackson 2.14.0
    * Apache CXF 3.5.4
    * Jetty 9.4.49.v20220914
    * Joda-Time 2.11.2
    * ActiveMQ 5.16.5
    * Hibernate 5.6.14
    * Log4J 2.19.0
    * Guava 31.1
    * Commons Codec 1.15
    * Mongo Java Server 1.42.0
    * Liquibase 4.17.2
    * Mongo Java Driver 4.7.0
    * Bson 2.13.1
    * Nimbus JOSE+JWT 9.23
    * JSON Small and Fast Parser 2.4.8
    * Swagger 2.2.3
    * etcd4j 2.18.0
    * Hazelcast 5.1.4
    * XDocReport 2.0.4
    * Bouncycastle Provider 1.72
    * Jacoco Maven Plugin 0.8.8

* Moved Mongo Java Server to optional dependency (in most cases Fake Mongo is not needed or can be added)
* BREAKING: replaced mongobee with Mongock (5.1.6), mostly backwards compatibility but configuration(see Class `MongoDBConfig`) and annotations (@Changelog and @ChangeSet) have to be replaced by Mongock equivalent https://docs.mongock.io/v5/features/legacy-migration/index.html
* InterconnectMapper: extension to allow registering modules and enabling/disabling features
* Fixing Bug in IVO Generator. Auditing and inheritence resulted in doubled fields(lastChange, lastChangeUser) and strange/unexpected behaviour. Fixed now. 
* Fixed problem in ActiveMQ with use of pooled connections on event listeners that causes avoidable regular reconnects
* Extend IdWithVersion with method toString() for better error logging 
* Log4jDaemonProperties.getCustomLevelMap no longer gives back immutableMap with empty configString. This was unexpected when you want to add values afterwards.

## 1.32
* Update dependencies
    * Spring 4.3.30
    * AWS 1.12.163
    * Jackson 2.12.6
    * Apache CXF 3.4.5
    * Jetty 9.4.45.v20220203
    * Joda-Time 2.10.13
    * slf4j 1.7.36
    * Powermock 2.0.9
    * Mockito 3.3.3
    * Junit 4.13.2
    * ActiveMQ 5.16.4
    * Hibernate 5.5.9
    * HTTPUtils 2.2
    * Concordion 3.1.3
    * Nimbus JOSE+JWT 9.20
    * JSON Small and Fast Parser 1.3.3
    * Liquibase 4.7.1
    * Mongo Java Driver 3.12.10
    * Jongo 1.5.0
    * Bson 2.12.0
    * Swagger 2.1.13
* BREAKING: commented out JasperReports to get the code compiling
* BREAKING: replaced Fongo with MongoDB Java Server 1.39.0 (https://github.com/bwaldvogel/mongo-java-server)
* Switched to Log4J2.x
* Optional additional parameter for PostgreSQL connection string
* Optional addition to use authentication with interconnect/ActiveMQ
* CloudConductorPropertyProvider: Allow setting https with configuration parameter
* Interconnect secury message crypto:
    * AES Keys are no longer Hex-encoded but Base64-encoded. This avoids limitation to only 16 characters.
    * BREAKING: Changed AES key encoding means potentially a new key is needed (some old keys work, some not)
    * Changed AES Block Mode from unsecure ECB to secure GCM

# Version 1.30
* Update dependencies
    * Spring 4.3.24
    * Jackson 2.9.8
    * AWS 1.11.553
    * Apache CXF 3.3.2
    * Jetty 9.4.18.v20190429
* add JWT provider for Auth0

# Version 1.29
* fixes ivo problem with propagating version
* Update dependencies
    * Spring 4.3.19
    * Jackson 2.9.7
    * AWS 1.11.414
    * Apache CXF 3.2.6
* BREAKING: Change `AReferenceableEntity` to interface. Extend `AEntity` additionally.
* lazy-loading and refresh for Cognito JWT keys
* add property provider for EC2 metadata service
* BREAKING: Migrate Swagger to OpenAPI
* add YAML mapper to JAX-RS

# Version 1.28
* Update dependencies
    * Spring 4.3.18
    * Jackson 2.9.6
    * Jodatime 2.10
    * AWS 1.11.362
    * Apache CXF 3.2.5
    * Restutils 1.9
* fixes event id deserialization bug in Interconnect

# Version 1.27
* **Update to Taimos HTTPUtils 2.0 (INCLUDING BREAKING CHANGES)**
* Fix `CognitoUser` (Broken in 1.26)
* Fix Event generation (some features where broken since 1.25)
* Refactor PropertyProvider in Dvalin 
* add full text search to `mongodb` module

# Version 1.26 (Cognito support is broken in this release)
* Integrate Taimos daemon framework into repository
* Update dependencies
    * Jongo 1.4.0
    * Jackson 2.9.5
    * Spring 4.3.15 (Security Release)
    * Apache CXF 3.2.4 (Bugfix Release)
    * Guava 24.1
    * AWS 1.11.309
* Adjust `DLinkQuery` to new data access
* Set JVM name for Instana to daemon name
* Fixed a Bug in interconnect DaemonScanner preventing Method discovery

# Version 1.25
* introduces yaml support to i18n
* add support for multiple resource loaders
* add method `count` to `MongoDBDataAccess`
* Add `CognitoContext`
* Update dependencies
    * Apache CXF 3.2.3
    * Jetty 9.4.8.v20171121
    * Daemon framework 2.15
* Fix InvocationInstance in `jaxrs` module
* bugfixes for ivo and event generation

# Version 1.24
* Update dependencies
    * AWS 1.11.292
    * Guava 24.0
    * Spring 4.3.14
    * Fongo 2.1.1
* Added support for internationalization, see the i18n module documentation
* Interconnect improvements:
    * Reworked IVO Generator for better readability and easier extensions
    * Introduced Event generation via interconnect-maven-plugin
    * Introduced Event support to interconnect-core 
* fixed a bug in jpa where liquibase could be executed after hibernate context was created
* fix swagger default URL to contain correct port (#60)
* MongoDB client improvements
    * refactor MongoDB data access to utility class
    * Auto detect entity class in `AbstractMongoDAO`

# Version 1.23
* Update dependencies
    * AWS 1.11.275
    * Spring 4.3.10
    * Jackson 2.8.10
* Create example implementations in extra repo
* Support EC2 Parameter Store as Config Source (#54)
* Support AWS Cognito as JWT source (#55)

# Version 1.22
* enhanced support to get versioned element from MongoDB DAOs
* improved and reworked GenericConverter
* adds an option to send messages to an alternative topic via IVORefreshSender
* fixes problem with last change user type and naming discrepancy

# Version 1.21
* Interconnect improvements
* add Audited version of MongoDB DAOs
* enhance Hazelcast configuration
* add configuration via environment variables
    * changed all properties to lowercase (backwards compatible)
* Update dependencies
    * Spring 4.3.8
    * JodaTime 2.9.9
    * AWS 1.11.140
    * Slf4j 1.7.25
    * Fongo 2.1.0
    * ActiveMQ 5.14.0
    * Daemon framework 2.14
    
# Version 1.20
* **Rework MongoDB support for new driver version (INCLUDING BREAKING CHANGES)**
* Update dependencies
    * AWS 1.11.90
    * Slf4j 1.7.23
    * Guava 21.0
    * MongoDB Driver 3.4.2
    * Mongobee 0.12
    * Fongo 2.0.12

# Version 1.19
* add `getRemoteAddress` method to `DvalinRSContext`
* Update dependencies
    * Spring 4.3.5
    * Daemon framework 2.13
    * Apache CXF 3.1.9
    * Jackson 2.8.6
    * AWS 1.11.86

# Version 1.18
* add JasperReports engine
* add abstract Zendesk JAX-RS endpoint
* add KMS encryption support
* Update dependencies
    * AWS 1.11.69
    * JodaTime 2.9.7
    * slf4j 1.7.22

# Version 1.17
* add WebAssert to assert WebExceptions in tests
* use DaemonFramework 2.12 with JSON log support
* Update dependencies
    * AWS 1.11.60

# Version 1.16
* Fix handling of InvalidParameterException on SNS endpoint creation
* add additional test helper
* move test helper from `jaxrs` to `test`
* Update dependencies
    * Spring 4.3.4
    * Powermock 1.6.6
    * Daemon framework 2.11
    * AWS 1.11.55
    * Jackson 2.8.5
    * JodaTime 2.9.6
    * Guava 20.0
    * Commons Beanutils 1.9.3
    * Apache CXF 3.1.8
    
# Version 1.15
* fix handling of different pushARN for different platforms (#47)

# Version 1.14
* Only start PushService when property is present (#45)
* Update dependencies
    * AWS 1.11.41
    * daemon framework 2.10
    * Spring 4.3.3
    * Jackson 2.8.3
    
# Version 1.13
* randomize database name for HSQL to get a new instance every time

# Version 1.12
* JAXRSContextImpl has to be normal component not Prod only (#42)
* added @TestComponent for PushService (#43)
* fixed some code quality issues

# Version 1.11
* refactor JAX-RS context
* AbstractAPI is now deprecated
* add structured logging
* add orchestration module (#38)
* add push service (#12)

# Version 1.10
* Update dependencies
    * daemon framework 2.9
    * Spring 4.3.2
    * Jackson 2.8.1
    * CXF 3.1.7
    * Mongo 3.3.0
    * Fongo 2.0.7
    * Hazelcast 3.7
    * AWS 1.11.27

# Version 1.9
* add XDocReport for PDF creation (#22)
* add library for Hazelcast clusters (#28)
* Change minimum version to Java 8 (#30)
* Update dependencies (#24)
    * Spring 4.3.1
    * Jackson 2.8.0
    * AWS 1.11.15

# Version 1.8
* add monitoring library with AWS CloudWatch support (#23)
* add dependecyManagement section to use dvalin-parent as BOM (#27)

# Version 1.7
* AWS tooling for ec2 and CloudFormation
* Document IAM actions needed in AWS
* Add support for custom AWS endpoints
* Add ISpringLifecycleListener (#19)
* Update dependencies
    * Jodatime 2.9.4
    * AWS 1.11.5

# Version 1.6
* fix @AWSClient credentials resolver 
* Auto configuration of dvalin Spring context
* add Maven archetype for basic dvalin project
* add Maven archetype for docker packaged dvalin project
* Update dependencies
    * daemon framework 2.7
    * Spring 4.2.6
    * Jackson 2.7.4
    * AWS 1.10.76

# Version 1.5
* add #4 JSON Web Token authentication support
* add dvalin-test library with injection tooling for unit tests of beans 
* add DynamoDB support for document storage
* Update dependencies
    * daemon framework 2.6
    * Slf4J 1.7.21
    * Jodatime 2.9.3
    * Jackson 2.7.3
    * Swagger 1.5.8
    * CXF 3.1.6
    * Jongo 1.3.0
    * bson4jackson 2.7.0

# Version 1.4
* add #9 automatic authentication filters for BasicAuth and TokenAuth
* add conditionals for bean availability and system properties
* add @LoggedIn annotation for SecurityContext assertions
* add abstract server-side WebSocketAdapter with JSON support
* add AWS Cloud tooling with @AWSClient autowiring
* add notification service to send emails with templating support

# Version 1.3
* Update 3rd-party libraries
    * Jodatime 2.9.2
    * Slf4J 1.7.18
    * Jackson 2.7.2
    * Swagger 1.5.7
    * CXF 3.1.5
    * Spring 4.2.5
    * Fongo 2.0.6
    * MongoDB 3.2.2

# Version 1.2
* Add Interconnect library (contribution by Cinovo AG http://www.cinovo.de)
* fix #6 add GridFS access method in MongoDB library
* fix #5 support skip and limit for MongoDB queries

# Version 1.1
* Fix broken Swagger endpoint

# Version 1.0
* Initial version
* Containing taimos/spring-cxf-daemon
* Containing taimos/spring-dao-mongo
* Containing taimos/spring-dao-hibernate
