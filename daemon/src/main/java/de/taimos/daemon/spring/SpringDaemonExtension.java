package de.taimos.daemon.spring;

import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.ILoggingConfigurer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class SpringDaemonExtension implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(SpringDaemonExtension.class);
    private SpringTest springTest;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        SpringDaemonExtension.logger.trace("Inject beans for " + context.getDisplayName());
        AutowireCapableBeanFactory beanFactory = this.springTest.getContext().getAutowireCapableBeanFactory();
        beanFactory.autowireBean(testInstance);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        SpringDaemonExtension.logger.trace("Setup " + extensionContext.getDisplayName());
        RunnerConfiguration cfgClass = this.findConfigAnnotation(extensionContext.getRequiredTestClass());
        if (cfgClass == null) {
            // Die on missing annotation
            throw new RuntimeException("Missing @RunnerConfiguration");
        }

        ILoggingConfigurer lc = cfgClass.loggingConfigurer().newInstance();
        lc.simpleLogging();

        RunnerConfig cfg = SpringDaemonExtension.getRunnerConfig(cfgClass);

        try {
            SpringDaemonExtension.this.springTest = this.createSpringTest(extensionContext, cfgClass, cfg);
        } catch (BeansException | IllegalStateException e) {
            SpringDaemonExtension.logger.error("Starting Spring context failed", e);
            throw new RuntimeException("Starting Spring context failed", e);
        }
        this.springTest.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        SpringDaemonExtension.logger.trace("Stop " + extensionContext.getDisplayName());
        this.springTest.stop();
    }

    private SpringTest createSpringTest(ExtensionContext extensionContext, RunnerConfiguration cfgClass, RunnerConfig cfg) {
        final Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
        return new SpringTest() {

            @Override
            protected String getServiceName() {
                return cfgClass.svc();
            }

            @Override
            protected void fillProperties(Map<String, String> props) {
                String servicePackage = cfg.getServicePackage();
                if (servicePackage != null) {
                    props.put(Configuration.SERVICE_PACKAGE, servicePackage);
                    System.setProperty(Configuration.SERVICE_PACKAGE, servicePackage);
                }

                Enumeration<?> names = cfg.getProps().propertyNames();
                while (names.hasMoreElements()) {
                    String key = (String) names.nextElement();
                    props.put(key, cfg.getProps().getProperty(key));
                }
            }

            @Override
            protected void doAfterSpringStart() {
                List<Method> methods = this.findAfterStartupAnnotatedMethods(requiredTestClass);
                for (Method method : methods) {
                    try {
                        method.invoke(SpringDaemonExtension.this.springTest.getContext().getBeanFactory()
                            .createBean(requiredTestClass));
                    } catch (Exception e) {
                        throw new RuntimeException("Starting Spring context failed", e);
                    }
                }
            }

            @Override
            protected String getSpringResource() {
                return cfg.getSpringFile();
            }

            private List<Method> findAfterStartupAnnotatedMethods(Class<?> clazz) {
                List<Method> res = new ArrayList<>();
                if (clazz == null) {
                    return res;
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(AfterStartup.class) && (method.getParameterTypes().length == 0)) {
                        res.add(method);
                    }
                }
                return res;
            }
        };
    }

    private static RunnerConfig getRunnerConfig(RunnerConfiguration cfgClass) throws InstantiationException, IllegalAccessException {
        final RunnerConfig cfg;
        cfg = cfgClass.config().newInstance();
        cfg.addProperty(DaemonProperties.SERVICE_NAME, cfgClass.svc());
        cfg.addProperty("profiles", "test");
        cfg.addProperty(DaemonProperties.STARTUP_MODE, DaemonProperties.STARTUP_MODE_DEV);
        return cfg;
    }

    private RunnerConfiguration findConfigAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        RunnerConfiguration cfgClass = clazz.getAnnotation(RunnerConfiguration.class);
        if (cfgClass == null) {
            cfgClass = this.findConfigAnnotation(clazz.getSuperclass());
        }
        return cfgClass;
    }

}
