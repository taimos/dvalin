## daemon

The `daemon` part includes the Taimos daemon framework into dvalin. You can use the following conditional annotations from the daemon framework:

* `@BeanAvailable` - Only create the annotated bean if the denoted bean is also available
* `@OnSystemProperty` - Only create the annotated bean if the denoted system property is set and if it optionally also has the given value

The entry point for your application is the `DvalinLifecycleAdapter`. Just extend it and implement a main method that calls the static `start` method. 
This configures your application to read properties from a file called `dvalin.properties`.
You can also use environment variables to configure Dvalin. `DVALIN_SOME_VARIABLE=foobar` becomes `some.variable=foobar`.
If environment variables are present the file is ignored.

By overriding the `setupLogging()` Method you can enable the `StructuredLogConfigurer` instead of the default Log4j configuration. 
Logging will the use the console and print all entries in JSON format. The `DvalinLogger` can be used to modify the MDC for a single log line.
