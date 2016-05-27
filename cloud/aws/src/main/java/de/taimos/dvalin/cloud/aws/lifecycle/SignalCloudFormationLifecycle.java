package de.taimos.dvalin.cloud.aws.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.cloud.aws.CloudFormation;
import de.taimos.dvalin.daemon.SpringLifecycleAdapter;

/**
 * signal CloudFormation if <i>aws.cfnsignal</i> property is set to <i>true</i>
 */
@Component
@OnSystemProperty(propertyName = "aws.cfnsignal", propertyValue = "true")
public class SignalCloudFormationLifecycle extends SpringLifecycleAdapter {

    @Autowired
    private CloudFormation cloudFormation;

    @Override
    public void started() {
        cloudFormation.signalReady();
    }
}
