package de.taimos.daemon.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IPropertyProvider} that checks multiple providers sequentially
 */
public class PropertyProviderChain implements IPropertyProvider {

    private final List<IPropertyProvider> providers = new ArrayList<>();

    public PropertyProviderChain withProvider(IPropertyProvider provider) {
        this.providers.add(provider);
        return this;
    }

    public PropertyProviderChain withProvider(Class<? extends IPropertyProvider> providerClass) throws IllegalAccessException, InstantiationException {
        return this.withProvider(providerClass.newInstance());
    }

    public PropertyProviderChain withProvider(String providerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return this.withProvider((Class<IPropertyProvider>) Class.forName(providerClass));
    }

    @Override
    public Map<String, String> loadProperties() {
        return this.providers.stream().map(IPropertyProvider::loadProperties).collect(HashMap::new, Map::putAll, Map::putAll);
    }
}
