/**
 *
 */
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

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
public final class URLUtils {

    private URLUtils() {
        // private utility class constructor
    }


    public static class SplitURL {

        private final String scheme;
        private final String host;
        private final String port;
        private final String path;


        private SplitURL(String scheme, String host, String port, String path) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.path = path;
        }

        public String getScheme() {
            return this.scheme;
        }

        public String getHost() {
            return this.host;
        }

        public String getPort() {
            return this.port;
        }

        public String getPath() {
            return this.path;
        }

    }


    public static SplitURL splitURL(String url) {
        if ((url == null) || url.isEmpty()) {
            throw new IllegalArgumentException("Invalid URL");
        }

        String scheme = null;
        String host = null;
        String port = null;
        String path = null;

        int iScheme = url.indexOf("://");
        int iPort;
        int iPath;

        if (iScheme != -1) {
            scheme = url.substring(0, iScheme);
            int beginIndex = iScheme + 3;
            String schemeless = url.substring(beginIndex);
            iPort = schemeless.contains(":") ? schemeless.indexOf(':') + beginIndex : -1;
            iPath = schemeless.contains("/") ? schemeless.indexOf('/') + beginIndex : -1;
        } else {
            scheme = "http";
            iPort = url.lastIndexOf(':');
            iPath = url.indexOf('/');
        }

        int startHost = iScheme == -1 ? 0 : iScheme + 3;
        int endHost = iPort == -1 ? (iPath == -1 ? url.length() : iPath) : iPort;
        host = url.substring(startHost, endHost);

        if (iPort != -1) {
            port = url.substring(iPort + 1, (iPath == -1 ? url.length() : iPath));
        } else {
            switch (scheme) {
            case "http":
            case "ws":
                port = "80";
                break;
            case "https":
            case "wss":
                port = "443";
                break;
            default:
                port = "";
                break;
            }
        }
        if (iPath != -1) {
            path = url.substring(iPath);
        } else {
            path = "/";
        }
        return new SplitURL(scheme, host, port, path);
    }

}
