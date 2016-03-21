package de.taimos.dvalin.daemon.conditional;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SystemPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(OnSystemProperty.class.getCanonicalName());
        if (attributes != null ) {
            Object name = attributes.get("propertyName");
            Object value = attributes.get("propertyValue");
            if (name != null && value != null) {
                String property = System.getProperty((String) name);
                if (property == null) {
                    return false;
                }
                if (value.toString().isEmpty()) {
                    return true;
                }
                return value.toString().equals(property);
            }
        }
        return false;
    }
}
