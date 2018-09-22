package de.taimos.dvalin.cloud.aws.properties;

import de.taimos.daemon.properties.BestEffortPropertyProviderChain;

public class AWSPropertyProvider extends BestEffortPropertyProviderChain {

    public AWSPropertyProvider() {
        this.withProvider(EC2MetadataPropertyProvider.class);
    }
}
