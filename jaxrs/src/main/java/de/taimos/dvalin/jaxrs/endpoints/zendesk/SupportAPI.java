/**
 *
 */
package de.taimos.dvalin.jaxrs.endpoints.zendesk;

/*-
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.taimos.dvalin.jaxrs.MapperFactory;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2016 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
@Consumes(MediaType.APPLICATION_JSON)
public abstract class SupportAPI<T extends ITicketRS> {

    @Value("${zendesk.subdomain}")
    private String subdomain;
    @Value("${zendesk.agent}")
    private String agentMail;
    @Value("${zendesk.token}")
    private String agentToken;

    @POST
    public Response createTicket(T ticket) {
        RESTAssert.assertNotEmpty(ticket.getRequesterMail());
        RESTAssert.assertNotEmpty(ticket.getRequesterMail());
        RESTAssert.assertNotEmpty(ticket.getSubject());
        RESTAssert.assertNotEmpty(ticket.getBody());

        Ticket tick = new Ticket();
        tick.setRequesterName(ticket.getRequesterName());
        tick.setRequesterEMail(ticket.getRequesterMail());
        tick.setSubject(ticket.getSubject());
        tick.setComment(ticket.getBody());

        this.customConversion(tick, ticket);

        this.createTicket(tick);

        return Response.accepted().build();
    }

    protected void customConversion(Ticket tick, T ticket) {
        //
    }

    private String createTicket(Ticket ticket) {
        final String json;
        try {
            json = MapperFactory.createDefault().writeValueAsString(ticket.toJsonMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String url = "https://" + this.subdomain + ".zendesk.com/api/v2/tickets.json";
        HTTPRequest req = WS.url(url).authBasic(this.agentMail + "/token", this.agentToken).contentType(MediaType.APPLICATION_JSON).body(json);
        try (HTTPResponse post = req.post()) {
            if (post.getStatus() != 201) {
                throw new InternalServerErrorException();
            }
            return post.getResponseAsString();
        }
    }

}
