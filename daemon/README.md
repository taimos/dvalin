## daemon

You can use the following conditional annotations:

* `@BeanAvailable` - Only create the annotated bean if the denoted bean is also available
* `@ClassAvailable` - Only create the annotated bean if the denoted class is on the classpath
* `@OnSystemProperty` - Only create the annotated bean if the denoted system property is set and if it optionally also has the given value

The entry point for your application is the `DvalinLifecycleAdapter`. Just extend it and implement a main method that calls the static `start` method. 
This configures your application to read properties from a file called `dvalin.properties`.
You can also use environment variables to configure Dvalin. `DVALIN_SOME_VARIABLE=foobar` becomes `some.variable=foobar`.
If environment variables are present the file is ignored.

The `DvalinLogger` can be used to modify the MDC for a single log line.

# Getting started

There are two different startup modes which can be specified with ``-DstartupMode=<mode>``:

- run: intended for foreground daemons
- dev: start in development mode

The default mode is ``dev``. If you want to run the program in _production mode_ make sure you start it with ``-DstartupMode=run``.

In development mode the program's behavior will differ in some small points:

- No Signal handlers are registered


## DaemonLifecycleAdapter

### void doStart() throws Exception

This method is called to start your program. It must be non blocking.
If this method throws any Exception the DaemonFramework stops execution of the daemon.

### void doStop() throws Exception

This method is called on OS signal to stop your program. It must be non blocking.
If this method throws any Exception the DaemonFramework stops execution of the daemon.

### started()

This method is called when the daemon completed startup successfully.

### stopped()

This method is called when the daemon completed shutdown successfully. Immediatly after this method _System.exit(0)_ is called.

### stopping()

This method is called when the daemon received shutdown signal.

### aborting()

This method is called when the daemon aborts execution. Immediatly after this method _System.exit(1)_ is called.

### signalUSR2()

This method is called when the daemon received the OS signal __USR2__. You can do what you want within this method.

### exception(LifecyclePhase phase, Throwable exception)

This method is called when an exception occurs in DaemonStarter. It provides the current phase and the thrown exception.

### Map<String, String> loadProperties()

This method is called to obtain the daemon properties. All properties provided in this map will be available via _System.getProperty_ later on.
The properties found in _de.taimos.daemon.DaemonProperties_ are used by the daemon framework itself and should be filled.

## Call DaemonStarter

In your _main_ method just call `startDaemon` to run the daemon framework. You have to provide a service name and an instance of your subclass of DaemonLifecycleAdapter;

```
DaemonStarter.startDaemon("my-service-name", new MyLifecycleAdapter());
```

## DvalinLifecycleAdapter

Extends the default `DaemonLifecycleAdapter` with defaults to a standard Dvalin Spring application.

### Lifecycle

Instead of using the lifecycle methods of the daemon framework, register beans of the type `ISpringLifecycleListener`. 
Davlin will call them with their correct lifecycle state.

### Properties

Dvalin register a `BestEffortPropertyProviderChain` and checks multiple locations for properties.

1) It lokks for the System property `property.source` and acts like the daemon framework default.
2) Loads properties from the file `dvalin.properties`
3) If the module `dvalin-cloud-aws-ssm` is used, values from SSM ParameterStore are loaded
4) Environment variables starting with `DVALIN_` are used. `DVALIN_SERVER_URL` becomes `server.url` 