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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.sns.model.InvalidParameterException;

public class AmazonEndpointCreationException {
    
    private final InvalidParameterException ex;
    private final String existingARN;
    
    public AmazonEndpointCreationException(InvalidParameterException ex) {
        this.ex = ex;
        
        Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists with the same Token.*");
        Matcher m = p.matcher(ex.getErrorMessage());
        if (m.matches()) {
            this.existingARN = m.group(1);
        } else {
            this.existingARN = null;
        }
    }
    
    public boolean didAlreadyExist() {
        return this.existingARN != null;
    }
    
    public InvalidParameterException getOriginalException() {
        return this.ex;
    }
    
    public String getExistingARN() {
        return this.existingARN;
    }
}
