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
