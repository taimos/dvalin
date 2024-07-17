package de.taimos.dvalin.jms.activemq;

import de.taimos.dvalin.jms.DvalinConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ClientInternalExceptionListener;
import org.apache.activemq.transport.TransportListener;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.ExceptionListener;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@SuppressWarnings("unused")
public class DvalinActiveMqConnectionFactory extends DvalinConnectionFactory {

    /**
     * Construct a {@link DvalinConnectionFactory} for ActiveMQ Classic, uses {@link DvalinConnectionFactory#getBrokerURL()} default broker url
     */
    public DvalinActiveMqConnectionFactory() {
        this(DvalinConnectionFactory.getBrokerURL());
    }

    /**
     * Construct a {@link DvalinConnectionFactory} for ActiveMQ Classic, uses the supplied broker url.
     *
     * @param brokerURL url for the broker
     */
    public DvalinActiveMqConnectionFactory(String brokerURL) {
        super(new ActiveMQConnectionFactory(brokerURL));
    }

    /**
     * Construct a {@link DvalinConnectionFactory} for ActiveMQ Classic, uses the supplied broker url.
     *
     * @param brokerURL url for the broker
     * @param userName  used by the broker
     * @param password  used by the broker
     */
    public DvalinActiveMqConnectionFactory(String brokerURL, String userName, String password) {
        super(new ActiveMQConnectionFactory(brokerURL), userName, password);
    }

    /**
     * @param listener exception listener
     */
    @Override
    public void setExceptionListener(ExceptionListener listener) {
        if (this.innerAdapter instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) this.innerAdapter).setExceptionListener(listener);
        } else if (this.innerAdapter instanceof UserCredentialsConnectionFactoryAdapter) {
            if (this.innerFactory instanceof ActiveMQConnectionFactory) {
                ((ActiveMQConnectionFactory) this.innerFactory).setExceptionListener(listener);
            }
        }
    }

    /**
     * @param trustAllPackages trust all packages, default false
     */
    @Override
    public void setTrustAllPackages(boolean trustAllPackages) {
        if (this.innerAdapter instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) this.innerAdapter).setTrustAllPackages(trustAllPackages);
        } else if (this.innerAdapter instanceof UserCredentialsConnectionFactoryAdapter) {
            if (this.innerFactory instanceof ActiveMQConnectionFactory) {
                ((ActiveMQConnectionFactory) this.innerFactory).setTrustAllPackages(trustAllPackages);
            }
        }

    }

    /**
     * @param listener client internal exception listener
     */
    public void setClientInternalExceptionListener(ClientInternalExceptionListener listener) {
        if (this.innerAdapter instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) this.innerAdapter).setClientInternalExceptionListener(listener);
        } else if (this.innerAdapter instanceof UserCredentialsConnectionFactoryAdapter) {
            if (this.innerFactory instanceof ActiveMQConnectionFactory) {
                ((ActiveMQConnectionFactory) this.innerFactory).setClientInternalExceptionListener(listener);
            }
        }

    }

    /**
     * @param listener transport exception listener
     */
    public void setTransportListener(TransportListener listener) {
        if (this.innerAdapter instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) this.innerAdapter).setTransportListener(listener);
        } else if (this.innerAdapter instanceof UserCredentialsConnectionFactoryAdapter) {
            if (this.innerFactory instanceof ActiveMQConnectionFactory) {
                ((ActiveMQConnectionFactory) this.innerFactory).setTransportListener(listener);
            }
        }
    }
}
