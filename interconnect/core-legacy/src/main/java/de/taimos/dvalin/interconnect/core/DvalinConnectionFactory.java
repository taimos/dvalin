package de.taimos.dvalin.interconnect.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ClientInternalExceptionListener;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author aeichel
 */
public class DvalinConnectionFactory implements ConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DvalinConnectionFactory.class);

    public static final String SYSPROP_USERNAME = "interconnect.jms.userName";
    public static final String SYSPROP_PASSWORD = "interconnect.jms.password";
    /**
     * name of the system property that contains the interconnect broker URL
     */
    public static final String SYSPROP_IBROKERURL = "interconnect.jms.broker";

    private ConnectionFactory innerFactory;
    private ConnectionFactory innerAdapter;

    private final String brokerURL;
    private final String userName;
    private final String password;

    /**
     *
     */
    public DvalinConnectionFactory() {
        String brokerURL = System.getProperty(DvalinConnectionFactory.SYSPROP_IBROKERURL);
        if (brokerURL == null) {
            DvalinConnectionFactory.LOGGER.warn(
                "No " + DvalinConnectionFactory.SYSPROP_IBROKERURL + " configured, using tcp://localhost:61616.");
            brokerURL = "tcp://localhost:61616";
        }
        this.userName = System.getProperty(DvalinConnectionFactory.SYSPROP_USERNAME);
        this.password = System.getProperty(DvalinConnectionFactory.SYSPROP_PASSWORD);
        this.brokerURL = brokerURL;
        this.init();
    }

    /**
     * @param brokerURL the broker URL
     */
    public DvalinConnectionFactory(String brokerURL) {
        this.userName = System.getProperty(DvalinConnectionFactory.SYSPROP_USERNAME);
        this.password = System.getProperty(DvalinConnectionFactory.SYSPROP_PASSWORD);
        this.brokerURL = brokerURL;
        this.init();
    }

    /**
     * @param brokerURL the broker URL
     * @param userName  the username
     * @param password  the password
     */
    public DvalinConnectionFactory(String brokerURL, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.brokerURL = brokerURL;
        this.init();
    }

    private void init() {
        this.innerFactory = new ActiveMQConnectionFactory(this.brokerURL);
        if (this.userName == null || this.userName.isEmpty()) {
            this.innerAdapter = this.innerFactory;
            return;
        }
        UserCredentialsConnectionFactoryAdapter credentialConnectionFactory = new UserCredentialsConnectionFactoryAdapter();
        credentialConnectionFactory.setTargetConnectionFactory(this.innerFactory);
        credentialConnectionFactory.setUsername(this.userName);
        credentialConnectionFactory.setPassword(this.password);
        this.innerAdapter = credentialConnectionFactory;
    }

    /**
     * @param listener exception listener
     */
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

    @Override
    public Connection createConnection() throws JMSException {
        return this.innerAdapter.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return this.innerAdapter.createConnection(userName, password);
    }
}
