package de.taimos.dvalin.notification.push;

/*-
 * #%L
 * Dvalin AWS notification service
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

import com.amazonaws.services.sns.model.InvalidParameterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AmazonEndpointCreationExceptionTest {

    @Test
    void shouldRethrow() {
        InvalidParameterException ex = new InvalidParameterException("Invalid parameter: Some other reason");
        AmazonEndpointCreationException aece = new AmazonEndpointCreationException(ex);

        Assertions.assertFalse(aece.didAlreadyExist());
        Assertions.assertNotNull(aece.getOriginalException());
    }

    @Test
    void shouldReturnExistingARN() {
        InvalidParameterException ex = new InvalidParameterException("Invalid parameter: Token Reason: Endpoint arn:aws:sns:eu-central-1:292004443359:endpoint/GCM/CycleballEU/367b766c-62cc-358b-adf5-b0f8c587ebad already exists with the same Token, but different attributes. (Service: AmazonSNS; Status Code: 400; Error Code: InvalidParameter; Request ID: 5d9a21dc-8f3c-57ca-ba6a-71a0c75ff128)");
        AmazonEndpointCreationException aece = new AmazonEndpointCreationException(ex);

        Assertions.assertTrue(aece.didAlreadyExist());
        Assertions.assertEquals("arn:aws:sns:eu-central-1:292004443359:endpoint/GCM/CycleballEU/367b766c-62cc-358b-adf5-b0f8c587ebad", aece.getExistingARN());
    }
}
