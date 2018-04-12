package de.taimos.dvalin.cloud.aws;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

import de.taimos.daemon.properties.IPropertyProvider;

public class ParameterStorePropertyProvider implements IPropertyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterStorePropertyProvider.class);

    @Override
    public Map<String, String> loadProperties() {
        try {
            AWSSimpleSystemsManagementClient client = new AWSClientFactory<AWSSimpleSystemsManagementClient>().create(AWSSimpleSystemsManagementClient.class);
            GetParametersByPathResult parameters = client.getParametersByPath(new GetParametersByPathRequest().withPath("/").withRecursive(true).withWithDecryption(true));

            return parameters.getParameters().stream().collect(Collectors.toMap(
                // strip path from parameter name
                parameter -> parameter.getName().substring(parameter.getName().lastIndexOf('/') + 1),
                Parameter::getValue
            ));
        } catch (Exception e) {
            LOGGER.info("Cannot load properties from SSM ParameterStore due to {}", e.getMessage());
        }
        return new HashMap<>();
    }

}
