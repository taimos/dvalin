package de.taimos.daemon.spring;

/*
 * #%L
 * Daemon Library Spring extension
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


import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.ILoggingConfigurer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SpringDaemonTestRunner extends BlockJUnit4ClassRunner {

    private static final Logger logger = LoggerFactory.getLogger(SpringDaemonTestRunner.class);

    private SpringTest springTest;


    /**
     * @param klass the class
     * @throws InitializationError on error
     */
    public SpringDaemonTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        final Statement invoker = super.methodInvoker(method, test);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                invoker.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        final Statement next = super.withAfterClasses(statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                next.evaluate();
                SpringDaemonTestRunner.this.springTest.stop();
            }
        };
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        final Statement next = super.withBeforeClasses(statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                final RunnerConfiguration cfgClass = this.findConfigAnnotation(
                    SpringDaemonTestRunner.this.getTestClass().getJavaClass());
                if (cfgClass == null) {
                    // Die on missing annotation
                    throw new RuntimeException("Missing @RunnerConfiguration");
                }

                ILoggingConfigurer lc = cfgClass.loggingConfigurer().newInstance();
                lc.simpleLogging();

                final RunnerConfig cfg;
                cfg = cfgClass.config().newInstance();
                cfg.addProperty(DaemonProperties.SERVICE_NAME, cfgClass.svc());
                cfg.addProperty("profiles", "test");
                cfg.addProperty(DaemonProperties.STARTUP_MODE, DaemonProperties.STARTUP_MODE_DEV);

                final AdditionalRunnerConfiguration addCfgClass = this.findAdditionalConfigAnnotation(
                    SpringDaemonTestRunner.this.getTestClass().getJavaClass());
                if (addCfgClass != null) {
                    SpringDaemonTestRunner.logger.trace("Adding additional configurations to test.");
                    for (Class<? extends TestConfiguration> aClass : addCfgClass.config()) {
                        aClass.newInstance().getProps().forEach((o, o2) -> cfg.addProperty((String) o, (String) o2));
                    }
                }
                try {
                    SpringDaemonTestRunner.this.springTest = this.getConfiguredSpringTest(cfgClass, cfg);
                    SpringDaemonTestRunner.this.springTest.start();
                    next.evaluate();
                } catch (BeansException | IllegalStateException e) {
                    SpringDaemonTestRunner.logger.error("Starting Spring context failed", e);
                    throw new RuntimeException("Starting Spring context failed", e);
                }
            }

            private SpringTest getConfiguredSpringTest(RunnerConfiguration cfgClass, RunnerConfig cfg) {
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
                    protected String getSpringResource() {
                        return cfg.getSpringFile();
                    }

                    @Override
                    protected void doAfterSpringStart() {
                        List<Method> methods = this.findAfterStartupAnnotatedMethods(
                            SpringDaemonTestRunner.this.getTestClass().getJavaClass());
                        for (Method method : methods) {
                            try {
                                method.invoke(SpringDaemonTestRunner.this.createTest());
                            } catch (Exception e) {
                                throw new RuntimeException("Starting Spring context failed", e);
                            }
                        }
                    }

                    private List<Method> findAfterStartupAnnotatedMethods(Class<?> clazz) {
                        List<Method> res = new ArrayList<>();
                        if (clazz == null) {
                            return res;
                        }
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(AfterStartup.class) &&
                                (method.getParameterTypes().length == 0)) {
                                res.add(method);
                            }
                        }
                        return res;
                    }

                };
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

            private AdditionalRunnerConfiguration findAdditionalConfigAnnotation(Class<?> clazz) {
                if (clazz == null) {
                    return null;
                }
                AdditionalRunnerConfiguration cfgClass = clazz.getAnnotation(AdditionalRunnerConfiguration.class);
                if (cfgClass == null) {
                    cfgClass = this.findAdditionalConfigAnnotation(clazz.getSuperclass());
                }
                return cfgClass;
            }

        };
    }

    @Override
    protected Object createTest() throws Exception {
        return this.springTest.getContext().getBeanFactory().createBean(this.getTestClass().getJavaClass());
    }


    /**
     * Copyright 2013 Cinovo AG<br>
     * <br>
     *
     * @author thoeger
     */
    public static class RunnerConfig implements TestConfiguration {

        private final Properties props = new Properties();


        /**
         * @param key   the prop key
         * @param value the prop value
         */
        protected void addProperty(final String key, final String value) {
            this.props.setProperty(key.trim(), value);
        }

        @Override
        public Properties getProps() {
            return this.props;
        }

        /**
         * @return the Spring file nme
         */
        public String getSpringFile() {
            return "spring-test/beans.xml";
        }

        /**
         * @return the package reference of the service
         */
        public String getServicePackage() {
            return null;
        }

        protected static Integer randomPort() {
            return (int) ((Math.random() * 20000) + 10000);
        }
    }

    /**
     * Copyright 2013 Cinovo AG<br>
     * <br>
     *
     * @author thoeger
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RunnerConfiguration {

        /**
         * @return the class
         */
        Class<? extends RunnerConfig> config() default RunnerConfig.class;

        /**
         * @return the service name
         */
        String svc();

        /**
         * @return the logging configurer
         */
        Class<? extends ILoggingConfigurer> loggingConfigurer();
    }

    /**
     * Copyright 2014 Taimos GmbH<br>
     * <br>
     *
     * @author thoeger
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AfterStartup {
        //
    }

    /**
     * Copyright 2023 Cinovo AG<br>
     * <br>
     *
     * @author fzwirn
     */
    public interface TestConfiguration {
        /**
         * @return the properties for this runner configuration
         */
        Properties getProps();
    }


    /**
     * Copyright 2023 Cinovo AG<br>
     * <br>
     *
     * @author fzwirn
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AdditionalRunnerConfiguration {
        /**
         * @return the class
         */
        Class<? extends TestConfiguration>[] config() default {};
    }
}