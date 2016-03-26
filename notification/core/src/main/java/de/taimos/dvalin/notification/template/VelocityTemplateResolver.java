package de.taimos.dvalin.notification.template;

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
public class VelocityTemplateResolver implements ITemplateResolver {

    @Autowired
    private VelocityEngineFactoryBean velocityEngineFactory;


    @Override
    public String resolveTemplate(String location, Map<String, Object> context) {
        Preconditions.checkArgument(location != null && !location.isEmpty());
        try {
            VelocityEngine engine = velocityEngineFactory.createVelocityEngine();
            return VelocityEngineUtils.mergeTemplateIntoString(engine, location, "UTF-8", context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String resolveRawTemplate(String template, Map<String, Object> context) {
        try {
            StringWriter result = new StringWriter();
            VelocityEngine engine = velocityEngineFactory.createVelocityEngine();

            VelocityContext velocityContext = new VelocityContext(context);
            engine.evaluate(velocityContext, result, "RawTemplate", template);
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
