package de.taimos.dvalin.test.jaxrs;

/*-
 * #%L
 * Test support for dvalin
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.taimos.dvalin.test.AssertErrors;

public class WebAssert {
    
    public static void assertErrorStatus(Response.Status status, AssertErrors.Execute func) {
        WebAssert.assertStatus(status.getStatusCode(), func);
    }
    
    public static void assertUnprocessableEntity(AssertErrors.Execute func) {
        WebAssert.assertStatus(422, func);
    }
    
    public static void assertNotFound(AssertErrors.Execute func) {
        WebAssert.assertErrorStatus(Response.Status.NOT_FOUND, func);
    }
    
    private static void assertStatus(int status, AssertErrors.Execute func) {
        AssertErrors.assertThrows(func, e -> {
            if (e instanceof WebApplicationException) {
                return ((WebApplicationException) e).getResponse().getStatus() == status;
            }
            return false;
        });
    }
    
}
