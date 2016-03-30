[![Build Status](https://travis-ci.org/taimos/dvalin.svg)](https://travis-ci.org/taimos/dvalin)
[![codecov.io](https://codecov.io/github/taimos/dvalin/coverage.svg?branch=master)](https://codecov.io/github/taimos/dvalin?branch=master)

# dvalin - Taimos Microservice Framework

Dvalin is a Java micro service framework based on several open-source frameworks to combine the best tools into one quick start suite for fast, reliable and scaling micro services.
The core technology is the Spring framework and dvalin uses our Daemon Framework as the lifecycle management for the service process.
 
# Parts of dvalin
 
Dvalin provides several independent but combineable libraries serving different purposes around the runtime and interaction of micro services.

These libraries are:

* `daemon` - the core library for lifecycle and basic Spring enhancements
* `jaxrs`- implement JAX-RS based REST services using the Apache CXF framework
* `jpa` - connect to SQL databases using the popular Hibernate framework
* `mongodb` - connect to MongoDB document store
* `cloud` - basic tools to communicate with Cloud providers
* `notification` - notification service to send e-mails and use template engines
* `interconnect` - communication framework to connect micro services with each other

## daemon

The `daemon` part includes the Taimos daemon framework into dvalin and adds two Spring conditional annotations to be used.

* `@BeanAvailable` - Only create the annotated bean if the denoted bean is also available
* `@OnSystemProperty` - Only create the annotated bean if the denoted system property is set and if it optionally also has the given value

## jaxrs

The `jaxrs` library adds tooling to implement JAX-RS web services. It combines the powers of the Spring framework, 
the Apache CXF framework, the Jackson JSON mapper and the Eclipse Jetty web server. To include it add the maven dependency 
and include the `spring/cxfdaemon.xml` file into your Spring context. It will then automatically configure a Jetty web server 
running on a given port and start a CXF JAX-RS handler into the running Jetty server. By default all beans annotated 
with `@JaxRsComponent` will be registered as provider or endpoint respectively. Additionally several extra features are implemented.

You can subclass the `AbstractAPI` class to get easier access to some of the features.  

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

To limit access to sensitive resources you can use the `SecurityContextUtil` that provides methods to retrieve 
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

For integration tests of your JAX-RS service there are several helpers implemented in dvalin. There 
is an abstract `APITest` class that provides several helper methods to start web requests, open web sockets
and assert responses from web service endpoints. To assert conditions across multiple threads you can use 
the AsyncAssert class. To inject client proxies to your API under test just annotate a field in your test 
class with `@TestProxy` and dvalin will create the desired proxy and link it to the server instance during test execution.  

## jpa

The `jpa` library adds JPA and Hibernate support including changeset management using liquibase. By setting some 
system properties and including the `spring/dao.xml` file into your Spring context you get the full support to 
store and retrieve data from relational database systems. Supported databases are currently HSQL, PostgreSQL and MySQL.
You have to add the desired jdbc driver to your classpath manually.

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

## cloud

## notification

## interconnect
