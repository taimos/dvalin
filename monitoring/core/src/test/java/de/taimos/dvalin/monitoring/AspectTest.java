package de.taimos.dvalin.monitoring;

/*-
 * #%L
 * Dvalin monitoring service
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.RunnerConfiguration;
import de.taimos.daemon.spring.SpringDaemonExtension;
import de.taimos.dvalin.daemon.DvalinTestRunnerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(SpringDaemonExtension.class)
@RunnerConfiguration(config = DvalinTestRunnerConfig.class, loggingConfigurer = Log4jLoggingConfigurer.class, svc = "AspectTest")
class AspectTest {

    @Autowired
    private TestBean bean;

    @Test
    void timeTest() throws Exception {
        this.bean.doSomething();
    }

    @Test
    void timeTestWithDimension() throws Exception {
        this.bean.doSomethingWithDimensions();
    }
}
