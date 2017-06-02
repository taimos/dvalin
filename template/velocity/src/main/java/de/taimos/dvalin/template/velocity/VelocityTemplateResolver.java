package de.taimos.dvalin.template.velocity;

/*-
 * #%L
 * Dvalin Velocity support
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.base.Preconditions;

@Component
// Velocity is deprecated since Spring 4.3
@SuppressWarnings("deprecation")
public class VelocityTemplateResolver implements ITemplateResolver {

    @Autowired
    private VelocityEngineFactoryBean velocityEngineFactory;


    @Override
    public String resolveTemplate(String location, Map<String, Object> context) {
        Preconditions.checkArgument(location != null && !location.isEmpty());
        try {
            VelocityEngine engine = this.velocityEngineFactory.createVelocityEngine();
            return VelocityEngineUtils.mergeTemplateIntoString(engine, location, "UTF-8", context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String resolveRawTemplate(String template, Map<String, Object> context) {
        try {
            StringWriter result = new StringWriter();
            VelocityEngine engine = this.velocityEngineFactory.createVelocityEngine();

            VelocityContext velocityContext = new VelocityContext(context);
            engine.evaluate(velocityContext, result, "RawTemplate", template);
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
