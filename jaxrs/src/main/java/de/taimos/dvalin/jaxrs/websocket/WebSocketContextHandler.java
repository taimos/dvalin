/**
 *
 */
package de.taimos.dvalin.jaxrs.websocket;

/*
 * #%L
 * Daemon with Spring and CXF
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

import javax.annotation.PostConstruct;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class WebSocketContextHandler extends ServletContextHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketContextHandler.class);

    @Value("${websocket.baseuri:/websocket}")
    private String baseURI;

    @Autowired
    private ListableBeanFactory beanFactory;


    @PostConstruct
    public void init() {
        this.setContextPath(this.baseURI);

        String[] socketBeans = this.beanFactory.getBeanNamesForAnnotation(WebSocket.class);
        for (String sb : socketBeans) {
            WebSocket ann = this.beanFactory.findAnnotationOnBean(sb, WebSocket.class);
            String pathSpec = ann.pathSpec();
            WebSocketContextHandler.LOGGER.info("Found bean {} for path {}", sb, pathSpec);
            this.addServlet(new ServletHolder(this.createServletForBeanName(sb)), pathSpec);
        }
    }

    private WebSocketServlet createServletForBeanName(final String beanName) {
        return new WebSocketServlet() {

            private static final long serialVersionUID = 1L;


            @Override
            public void configure(WebSocketServletFactory factory) {
                WebSocketContextHandler.LOGGER.info("Configuring WebSocket Servlet for {}", beanName);
                factory.getPolicy().setIdleTimeout(10000);
                factory.setCreator((req, resp) -> WebSocketContextHandler.this.beanFactory.getBean(beanName));
            }
        };
    }

}
