/**
 *
 */
package de.taimos.daemon;

/*
 * #%L
 * Daemon Library
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;

/**
 * @author hoegertn
 */
@SuppressWarnings("restriction")
public class DaemonStarter {

    private static final AtomicReference<String> daemonName = new AtomicReference<>();

    private static final String instanceId = UUID.randomUUID().toString();

    private static final Properties daemonProperties = new Properties();

    private static final AtomicReference<String> hostname = new AtomicReference<>();

    private static final DaemonManager daemon = new DaemonManager();

    private static final AtomicReference<String> startupMode = new AtomicReference<>(DaemonProperties.STARTUP_MODE_DEV);

    private static final Logger rlog = LoggerFactory.getLogger(DaemonStarter.class);

    private static final AtomicReference<ILoggingConfigurer> loggingConfigurer = new AtomicReference<>();

    private static final AtomicReference<IDaemonLifecycleListener> lifecycleListener = new AtomicReference<>();

    private static final AtomicReference<LifecyclePhase> currentPhase = new AtomicReference<>(LifecyclePhase.STOPPED);

    /**
     * @return if the system is in development mode
     */
    public static boolean isDevelopmentMode() {
        return DaemonStarter.startupMode.get().equals(DaemonProperties.STARTUP_MODE_DEV);
    }

    /**
     * @return if the system is in run mode
     */
    public static boolean isRunMode() {
        return DaemonStarter.startupMode.get().equals(DaemonProperties.STARTUP_MODE_RUN);
    }

    /**
     * @return the startup mode
     */
    public static String getStartupMode() {
        return DaemonStarter.startupMode.get();
    }

    /**
     * @return the current {@link LifecyclePhase}
     */
    public static LifecyclePhase getCurrentPhase() {
        return DaemonStarter.currentPhase.get();
    }

    /**
     * @return the hostname of the running machine
     */
    public static String getHostname() {
        return DaemonStarter.hostname.get();
    }

    /**
     * @return the name of this daemon
     */
    public static String getDaemonName() {
        return DaemonStarter.daemonName.get();
    }

    /**
     * @return the instance UUID for this daemon
     */
    public static String getInstanceId() {
        return DaemonStarter.instanceId;
    }

    /**
     * @return the daemon properties
     */
    public static Properties getDaemonProperties() {
        return DaemonStarter.daemonProperties;
    }

    private static IDaemonLifecycleListener getLifecycleListener() {
        if (DaemonStarter.lifecycleListener.get() == null) {
            throw new RuntimeException("No lifecycle listener found");
        }
        return DaemonStarter.lifecycleListener.get();
    }

    /**
     * Starts the daemon and provides feedback through the life-cycle listener<br>
     * <br>
     *
     * @param _daemonName        the name of this daemon
     * @param _lifecycleListener the {@link IDaemonLifecycleListener} to use for phase call-backs
     */
    public static void startDaemon(final String _daemonName, final IDaemonLifecycleListener _lifecycleListener) {
        // Run de.taimos.daemon async
        Executors.newSingleThreadExecutor().execute(() -> DaemonStarter.doStartDaemon(_daemonName, _lifecycleListener));
    }

    private static void doStartDaemon(final String _daemonName, final IDaemonLifecycleListener _lifecycleListener) {
        final boolean updated = DaemonStarter.currentPhase.compareAndSet(LifecyclePhase.STOPPED, LifecyclePhase.STARTING);
        if (!updated) {
            DaemonStarter.rlog.error("Service already running");
            return;
        }

        DaemonStarter.daemonName.set(_daemonName);
        DaemonStarter.addProperty(DaemonProperties.DAEMON_NAME, _daemonName);
        DaemonStarter.addProperty(DaemonProperties.SERVICE_NAME, _daemonName);
        DaemonStarter.addProperty("com.instana.agent.jvm.name", _daemonName);
        DaemonStarter.lifecycleListener.set(_lifecycleListener);

        DaemonStarter.startupMode.set(System.getProperty(DaemonProperties.STARTUP_MODE, DaemonProperties.STARTUP_MODE_DEV));

        // Configure the logging subsystem
        DaemonStarter.configureLogging();

        // Set and check the host name
        DaemonStarter.determineHostname();

        // Print startup information to log
        DaemonStarter.logStartupInfo();

        // handle system signals like HUP, TERM, USR2
        DaemonStarter.handleSignals();

        // Load properties
        DaemonStarter.initProperties();

        // Configure DNS TTL
        if (DaemonStarter.getDaemonProperties().getProperty(DaemonProperties.DNS_TTL) != null) {
            DaemonStarter.rlog.info("Setting JVM DNS TTL: {}", DaemonStarter.getDaemonProperties().getProperty(DaemonProperties.DNS_TTL));
            java.security.Security.setProperty("networkaddress.cache.ttl", DaemonStarter.getDaemonProperties().getProperty(DaemonProperties.DNS_TTL));
        }

        // Reconfigure logging with properties
        try {
            if (DaemonStarter.loggingConfigurer.get() != null) {
                DaemonStarter.loggingConfigurer.get().reconfigureLogging();
            }
        } catch (final Exception e) {
            System.err.println("Logger reconfig failed with exception: " + e.getMessage());
            DaemonStarter.getLifecycleListener().exception(DaemonStarter.currentPhase.get(), e);
        }

        // Run custom startup code
        try {
            DaemonStarter.getLifecycleListener().doStart();
        } catch (Exception e) {
            DaemonStarter.abortSystem(e);
        }

        // Daemon has been started
        DaemonStarter.notifyStarted();

        // This blocks until stop() is called
        DaemonStarter.daemon.block();

        // Shutdown system
        try {
            DaemonStarter.getLifecycleListener().doStop();
        } catch (Exception e) {
            DaemonStarter.abortSystem(e);
        }

        // Daemon has been stopped
        DaemonStarter.notifyStopped();

        // Exit system with success return code
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    private static void configureLogging() {
        String clazz = System.getProperty(DaemonProperties.LOGGER_CONFIGURER);
        if (clazz != null) {
            try {
                Class<ILoggingConfigurer> configurerClazz = (Class<ILoggingConfigurer>) Class.forName(clazz);
                DaemonStarter.loggingConfigurer.set(configurerClazz.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                DaemonStarter.rlog.warn("No ILoggingConfigurer set");
            }
        }
        try {
            if (DaemonStarter.loggingConfigurer.get() != null) {
                DaemonStarter.loggingConfigurer.get().initializeLogging();
            }
        } catch (final Exception e) {
            System.err.println("Logger config failed with exception: " + e.getMessage());
            DaemonStarter.getLifecycleListener().exception(DaemonStarter.currentPhase.get(), e);
        }
    }

    private static void notifyStopped() {
        DaemonStarter.currentPhase.set(LifecyclePhase.STOPPED);
        DaemonStarter.rlog.info("{} stopped!", DaemonStarter.daemonName);
        DaemonStarter.getLifecycleListener().stopped();
    }

    private static void notifyStarted() {
        DaemonStarter.currentPhase.set(LifecyclePhase.STARTED);
        DaemonStarter.rlog.info("{} started!", DaemonStarter.daemonName);
        DaemonStarter.getLifecycleListener().started();
    }

    private static void logStartupInfo() {
        if (DaemonStarter.isDevelopmentMode()) {
            DaemonStarter.rlog.info("Running in development mode");
        } else {
            DaemonStarter.rlog.info("Running in production mode");
        }

        DaemonStarter.rlog.info("Running with instance id: {}", DaemonStarter.instanceId);
        DaemonStarter.rlog.info("Running on host: {}", DaemonStarter.hostname);
    }

    private static void determineHostname() {
        try {
            final String host = InetAddress.getLocalHost().getHostName();
            if ((host != null) && !host.isEmpty()) {
                DaemonStarter.hostname.set(host);
            } else {
                DaemonStarter.rlog.error("Hostname could not be determined --> Exiting");
                DaemonStarter.abortSystem();
            }
        } catch (final UnknownHostException e) {
            DaemonStarter.rlog.error("Getting hostname failed", e);
            DaemonStarter.abortSystem(e);
        }
    }

    private static void initProperties() {
        try {
            // Loading properties
            final Map<String, String> properties = DaemonStarter.getLifecycleListener().loadProperties();
            if (properties != null) {
                for (final Entry<String, String> e : properties.entrySet()) {
                    DaemonStarter.addProperty(e.getKey(), String.valueOf(e.getValue()));
                }
            }
        } catch (final Exception e) {
            DaemonStarter.rlog.error("Getting config data failed", e);
            DaemonStarter.abortSystem(e);
        }
    }

    private static void addProperty(final String key, final String value) {
        if (key == null) {
            return;
        }
        String trimKey = key.trim();
        if (DaemonStarter.isDevelopmentMode()) {
            DaemonStarter.rlog.info("Setting property: '{}' with value '{}'", trimKey, value);
        } else {
            DaemonStarter.rlog.debug("Setting property: '{}' with value '{}'", trimKey, value);
        }
        DaemonStarter.daemonProperties.setProperty(trimKey, value);
        System.setProperty(trimKey, value);
    }

    /**
     * Stop the service and end the program
     */
    public static void stopService() {
        DaemonStarter.currentPhase.set(LifecyclePhase.STOPPING);
        final CountDownLatch cdl = new CountDownLatch(1);
        Executors.newSingleThreadExecutor().execute(() -> {
            DaemonStarter.getLifecycleListener().stopping();
            DaemonStarter.daemon.stop();
            cdl.countDown();
        });

        try {
            int timeout = DaemonStarter.lifecycleListener.get().getShutdownTimeoutSeconds();
            if (!cdl.await(timeout, TimeUnit.SECONDS)) {
                DaemonStarter.rlog.error("Failed to stop gracefully");
                DaemonStarter.abortSystem();
            }
        } catch (InterruptedException e) {
            DaemonStarter.rlog.error("Failure awaiting stop", e);
            Thread.currentThread().interrupt();
        }

    }

    // I KNOW WHAT I AM DOING
    private static void handleSignals() {
        if (DaemonStarter.isRunMode()) {
            try {
                // handle SIGHUP to prevent process to get killed when exiting the tty
                Signal.handle(new Signal("HUP"), arg0 -> {
                    // Nothing to do here
                    System.out.println("SIG INT");
                });
            } catch (IllegalArgumentException e) {
                System.err.println("Signal HUP not supported");
            }

            try {
                // handle SIGTERM to notify the program to stop
                Signal.handle(new Signal("TERM"), arg0 -> {
                    System.out.println("SIG TERM");
                    DaemonStarter.stopService();
                });
            } catch (IllegalArgumentException e) {
                System.err.println("Signal TERM not supported");
            }

            try {
                // handle SIGINT to notify the program to stop
                Signal.handle(new Signal("INT"), arg0 -> {
                    System.out.println("SIG INT");
                    DaemonStarter.stopService();
                });
            } catch (IllegalArgumentException e) {
                System.err.println("Signal INT not supported");
            }

            try {
                // handle SIGUSR2 to notify the life-cycle listener
                Signal.handle(new Signal("USR2"), arg0 -> {
                    System.out.println("SIG USR2");
                    DaemonStarter.getLifecycleListener().signalUSR2();
                });
            } catch (IllegalArgumentException e) {
                System.err.println("Signal USR2 not supported");
            }
        }
    }

    /**
     * Abort the daemon
     */
    public static void abortSystem() {
        DaemonStarter.abortSystem(null);
    }

    /**
     * Abort the daemon
     *
     * @param error the error causing the abortion
     */
    public static void abortSystem(final Throwable error) {
        DaemonStarter.currentPhase.set(LifecyclePhase.ABORTING);
        try {
            DaemonStarter.getLifecycleListener().aborting();
        } catch (Exception e) {
            DaemonStarter.rlog.error("Custom abort failed", e);
        }
        if (error != null) {
            DaemonStarter.rlog.error("Unrecoverable error encountered  --> Exiting", error);
            DaemonStarter.getLifecycleListener().exception(LifecyclePhase.ABORTING, error);
        } else {
            DaemonStarter.rlog.error("Unrecoverable error encountered --> Exiting");
        }
        // Exit system with failure return code
        System.exit(1);
    }

    private DaemonStarter() {
        // Hide it
    }
}
