package de.taimos.dvalin.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.annotation.Nonnull;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSContext;
import javax.jms.JMSException;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author aeichel, fzwirn
 */
@SuppressWarnings("unused")
public class DvalinConnectionFactory implements ConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DvalinConnectionFactory.class);

    public static final String SYSPROP_USERNAME = "interconnect.jms.userName";
    public static final String SYSPROP_PASSWORD = "interconnect.jms.password";
    /**
     * name of the system property that contains the interconnect broker URL
     */
    public static final String SYSPROP_IBROKERURL = "interconnect.jms.broker";

    private static final String FALLBACK_BROKER_URL = "tcp://localhost:61616";

    protected ConnectionFactory innerFactory;
    protected ConnectionFactory innerAdapter;

    private final String userName;
    private final String password;

    /**
     * Uses {@link DvalinConnectionFactory#SYSPROP_IBROKERURL} as a broker url, or a default broker {@link DvalinConnectionFactory#FALLBACK_BROKER_URL}.
     *
     * @return a broker url
     */
    public static String getBrokerURL() {
        String brokerURL = System.getProperty(DvalinConnectionFactory.SYSPROP_IBROKERURL);
        if (brokerURL == null) {
            DvalinConnectionFactory.LOGGER.warn("No {} configured, using default {}",
                DvalinConnectionFactory.SYSPROP_IBROKERURL, DvalinConnectionFactory.FALLBACK_BROKER_URL);
            brokerURL = DvalinConnectionFactory.FALLBACK_BROKER_URL;
        }
        return brokerURL;
    }

    /**
     * @param innerFactory the connection factory used by this wrapper.
     */
    public DvalinConnectionFactory(@Nonnull ConnectionFactory innerFactory) {
        this(innerFactory, System.getProperty(DvalinConnectionFactory.SYSPROP_USERNAME),
            System.getProperty(DvalinConnectionFactory.SYSPROP_PASSWORD));
    }

    /**
     * @param innerFactory the connection factory used by this wrapper.
     * @param userName     the username
     * @param password     the password
     */
    public DvalinConnectionFactory(@Nonnull ConnectionFactory innerFactory, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.init(innerFactory);
    }

    protected void init(@Nonnull ConnectionFactory innerFactory) {
        this.innerFactory = innerFactory;
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
        // Implement if needed
    }

    /**
     * @param trustAllPackages trust all packages, default false
     */
    public void setTrustAllPackages(boolean trustAllPackages) {
        // Implement if needed
    }

    /**
     * Start the connection factory
     */
    public void start() {
        // Implement if needed
    }

    /**
     * Stop the connection factory
     */
    public void stop() {
        // Implement if needed
    }

    @Override
    public Connection createConnection() throws JMSException {
        return this.innerAdapter.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return this.innerAdapter.createConnection(userName, password);
    }

    @Override
    public JMSContext createContext() {
        return this.innerAdapter.createContext();
    }

    @Override
    public JMSContext createContext(String userName, String password) {
        return this.innerAdapter.createContext(userName, password);
    }

    @Override
    public JMSContext createContext(String userName, String password, int sessionMode) {
        return this.innerAdapter.createContext(userName, password, sessionMode);
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        return this.innerAdapter.createContext(sessionMode);
    }
}
