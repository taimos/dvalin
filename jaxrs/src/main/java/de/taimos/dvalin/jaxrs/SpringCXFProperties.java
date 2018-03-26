package de.taimos.dvalin.jaxrs;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 Taimos GmbH
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

public interface SpringCXFProperties {

    /**
     * the port to bind to <br>
     * <strong>default:</strong> 8080
     */
    String JAXRS_BINDPORT = "jaxrs.bindport";

    /**
     * the host to bind to <br>
     * <strong>default:</strong> 0.0.0.0
     */
    String JAXRS_BINDHOST = "jaxrs.bindhost";

    /**
     * the base path of the JAX-RS endpoint <br>
     * <strong>default:</strong>
     */
    String JAXRS_PATH = "jaxrs.path";

    /**
     * the service annotation <br>
     * <strong>default:</strong> JaxRsComponent
     */
    String JAXRS_ANNOTATION = "jaxrs.annotation";

    /**
     * the duration threshold before logging slow requests <br>
     * <strong>default:</strong> 1000 ms
     */
    String JAXRS_SLOWLOG = "jaxrs.slowlog";

    /**
     * send the jetty version as Response Header <br>
     * <strong>default:</strong> false
     */
    String JETTY_SENDVERSION = "jetty.sendVersion";

    /**
     * minimum number of jetty threads <br>
     * <strong>default:</strong> 5
     */
    String JETTY_MINTHREADS = "jetty.minThreads";

    /**
     * maximum number of jetty threads <br>
     * <strong>default:</strong> 150
     */
    String JETTY_MAXTHREADS = "jetty.MaxThreads";

    /**
     * use Jetty sessions <br>
     * <strong>default:</strong> false
     */
    String JETTY_SESSIONS = "jetty.sessions";

    /**
     * the public base URL of the server<br>
     * <strong>default:</strong> http://localhost:&lt;port&gt;
     */
    String SERVER_URL = "server.url";

    /**
     * the class to use as default handler <br>
     * <strong>default:</strong> org.eclipse.jetty.server.handler.DefaultHandler
     */
    String DEFAULT_HANDLER = "defaultHandlerClass";

}
