## jaxrs

The `jaxrs` library adds tooling to implement JAX-RS web services. It combines the powers of the Spring framework, 
the Apache CXF framework, the Jackson JSON mapper and the Eclipse Jetty web server. To include it add the maven dependency to your POM file.
When the `http` spring profile is active, a Jetty web server will be started on a given port and start a CXF JAX-RS handler into the running Jetty server. 
By default all beans annotated with `@JaxRsComponent` will be registered as provider or endpoint respectively. 
Additionally several extra features are implemented.

You can inject the `DvalinRSContext` to get easier access to some of the features.  

### Monitoring

For every request an instance of `de.taimos.dvalin.jaxrs.monitoring.InvocationInstance` will be registered into the MessageContext. 
It will contain a unique request id and will measure the runtime of the request for monitoring purpose.
It also populates the request id field of the context.

### Providers

You can activate preconfigured JacksonProvider or WebExceptionMapper by subclassing the respective type and annotate it with `@JaxRsComponent`.

### Remote service

By annotating a private field of a JAX-RS client interface with `@RemoteService` dvalin will automatically create a JAX-RS client 
proxy and then inject it like any other autowired bean into the desired location. You can provide the base URL of the service 
or use defined system properties to resolve the URL of the service.

### Swagger

moved to `jaxrs-swagger` module

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
