package de.taimos.daemon.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link PropertyProviderChain} that does not fail for invalid providers
 */
public class BestEffortPropertyProviderChain extends PropertyProviderChain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestEffortPropertyProviderChain.class);

    @Override
    public PropertyProviderChain withProvider(Class<IPropertyProvider> providerClass) {
        try {
            return super.withProvider(providerClass);
        } catch (IllegalAccessException | InstantiationException e) {
            LOGGER.info("Cannot create IPropertyProvider due to {}", e.getMessage());
        }
        return this;
    }

    @Override
    public PropertyProviderChain withProvider(String providerClass) {
        try {
            return super.withProvider(providerClass);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            LOGGER.info("Cannot create IPropertyProvider due to {}", e.getMessage());
        }
        return this;
    }

}
