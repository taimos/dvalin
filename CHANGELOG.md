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
