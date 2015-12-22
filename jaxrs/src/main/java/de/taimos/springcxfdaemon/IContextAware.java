package de.taimos.springcxfdaemon;

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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.ext.MessageContext;

public interface IContextAware {
	
	@Context
	public abstract void setMessageContext(MessageContext context);
	
	@Context
	public abstract void setHttpServletRequest(HttpServletRequest request);
	
	@Context
	public abstract void setHttpServletResponse(HttpServletResponse response);
	
}