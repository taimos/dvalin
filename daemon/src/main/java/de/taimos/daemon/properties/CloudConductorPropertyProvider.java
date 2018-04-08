package de.taimos.daemon.properties;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.taimos.daemon.DaemonStarter;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;

public class CloudConductorPropertyProvider extends HTTPPropertyProvider {

	public static final String CLOUDCONDUCTOR_URL = "CLOUDCONDUCTOR_URL";
	public static final String TEMPLATE_NAME = "TEMPLATE_NAME";
	public static final String CLOUDCONDUCTOR_TOKEN = "CLOUDCONDUCTOR_TOKEN";

	public static final String CLOUDCONDUCTOR_PROP_FILE = "CLOUDCONDUCTOR_PROP_FILE";
	public static final String CLOUDCONDUCTOR_PROP_FILE_TOKEN = "AUTH_TOKEN";
	public static final String CLOUDCONDUCTOR_PROP_FILE_DEFAULT_PATH = "/opt/cloudconductor-agent/cloudconductor-agent.properties";

	private String server;
	private String template;
	private String jwt;

	private String protocol = "http";

	public CloudConductorPropertyProvider() {
		this(System.getenv(CloudConductorPropertyProvider.CLOUDCONDUCTOR_URL), System.getenv(CloudConductorPropertyProvider.TEMPLATE_NAME), System.getenv(CloudConductorPropertyProvider.CLOUDCONDUCTOR_TOKEN));
	}

	public CloudConductorPropertyProvider(String propFile) {
		this.readPropertyFile(propFile);
	}

	public CloudConductorPropertyProvider(String server, String template) {
		this(server, template, null);
	}

	public CloudConductorPropertyProvider(String server, String template, String token) {
		this.server = server;
		this.template = template;
		this.jwt = this.getAuthToken(token);
		this.readPropertyFile(System.getenv(CloudConductorPropertyProvider.CLOUDCONDUCTOR_PROP_FILE));
	}

	/**
	 * @param protocol http or https, http is default;
	 * @return this provider
	 */
	public CloudConductorPropertyProvider setProtocol(String protocol) {
		if(protocol.equalsIgnoreCase("https")) {
			this.protocol = "https";
		} else {
			this.protocol = "http";
		}
		return this;
	}

	@Override
	protected String getDescription() {
		return String.format("CloudConductor Server %s with template %s", this.server, this.template);
	}

	@Override
	protected HTTPResponse getResponse() {
		HTTPRequest req = WS.url(this.protocol + "://" + this.server + "/api/config/{template}/{svc}");
		req.pathParam("template", this.template).pathParam("svc", DaemonStarter.getDaemonName());
		req.accept("application/x-javaprops");
		if(this.jwt != null) {
			req.authBearer(this.jwt);
		}
		return req.get();
	}

	private void readPropertyFile(String propFile) {
		File file = null;
		if(propFile != null && !propFile.isEmpty()) {
			file = new File(propFile);
		}
		if(file == null || !file.exists()) {
			file = new File(CloudConductorPropertyProvider.CLOUDCONDUCTOR_PROP_FILE_DEFAULT_PATH);
		}
		if(file.exists()) {
			try(InputStream reader = new FileInputStream(file)) {
				Properties prop = new Properties();
				prop.load(reader);
				if(this.server == null && prop.containsKey(CloudConductorPropertyProvider.CLOUDCONDUCTOR_URL)) {
					this.server = prop.getProperty(CloudConductorPropertyProvider.CLOUDCONDUCTOR_URL);
				}
				if(this.template == null && prop.containsKey(CloudConductorPropertyProvider.TEMPLATE_NAME)) {
					this.template = prop.getProperty(CloudConductorPropertyProvider.TEMPLATE_NAME);
				}
				if(this.jwt == null && prop.containsKey(CloudConductorPropertyProvider.CLOUDCONDUCTOR_PROP_FILE_TOKEN)) {
					this.jwt = this.getAuthToken(prop.getProperty(CloudConductorPropertyProvider.CLOUDCONDUCTOR_PROP_FILE_TOKEN));
				}
			} catch(IOException ex) {
				this.logger.warn("Failed to find cloudconductor properties file: '{}'", file);
			}
		}
	}

	private String getAuthToken(String token) {
		if(token == null || token.isEmpty()) {
			return null;
		}
		String path = this.protocol + "://" + this.server + "/api/auth";
		String body = "{\"token\":\"" + token + "\"}";
        HTTPRequest httpRequest = WS.url(path).body(body).header("Content-Type", "application/json;charset=UTF-8");
        try (HTTPResponse response = httpRequest.put()) {
            int status = response.getStatus();
            if (200 <= status && 300 > status) {
                String responseAsString = response.getResponseAsString();
                if (responseAsString.startsWith("\"")) {
                    responseAsString = responseAsString.substring(1);
                }
                if (responseAsString.endsWith("\"")) {
                    responseAsString = responseAsString.substring(0, responseAsString.length() - 1);
                }
                return responseAsString;
            } else {
                this.logger.warn("Authentication with CloudConductor Server {} failed with status {}", this.server, status);
            }
        }
		return null;
	}
}
