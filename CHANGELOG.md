# current master
* **Update to Taimos HTTPUtils 2.0 (INCLUDING BREAKING CHANGES)**
* Fix `CognitoUser` (Broken in 1.26)
* Fix Event generation (some features where broken since 1.25)
* Refactor PropertyProvider in Dvalin 

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
