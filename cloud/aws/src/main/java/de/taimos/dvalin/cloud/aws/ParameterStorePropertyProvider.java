package de.taimos.dvalin.cloud.aws;

import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.daemon.properties.EnvPropertyProvider;

public class ParameterStorePropertyProvider implements IPropertyProvider {

    @Override
    public Map<String, String> loadProperties() {

        AWSSimpleSystemsManagementClient client = new AWSClientFactory<AWSSimpleSystemsManagementClient>().create(AWSSimpleSystemsManagementClient.class);
        GetParametersByPathResult parameters = client.getParametersByPath(new GetParametersByPathRequest().withPath("/").withRecursive(true).withWithDecryption(true));

        Map<String, String> map = parameters.getParameters().stream().collect(Collectors.toMap(
            // strip path from parameter name
            parameter -> parameter.getName().substring(parameter.getName().lastIndexOf('/') + 1),
            Parameter::getValue
        ));

        // Override with ENV variables
        map.putAll(new EnvPropertyProvider().loadProperties());

        return map;
    }

}
