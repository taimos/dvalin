package de.taimos.dvalin.daemon.conditional;

import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BeanAvailableCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(BeanAvailable.class.getCanonicalName());
        if (attributes != null ) {
            Object value = attributes.get("value");
            if (value != null && value instanceof Class) {
                String[] beans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context.getBeanFactory(), (Class<?>) value);
                return beans.length > 0;
            }
        }
        return false;
    }

}
