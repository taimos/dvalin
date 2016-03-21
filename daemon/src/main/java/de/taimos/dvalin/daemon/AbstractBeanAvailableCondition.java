package de.taimos.dvalin.daemon;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class AbstractBeanAvailableCondition<T> implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] beans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context.getBeanFactory(), getBeanClass());
        return beans.length > 0;
    }

    protected abstract Class<T> getBeanClass();

}
