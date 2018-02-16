package de.taimos.dvalin.cloud.aws;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.google.common.base.Joiner;

class ProxyConfiguration {
    
    static final String HTTP_PROXY = "HTTP_PROXY";
    static final String HTTP_PROXY_LC = "http_proxy";
    static final String HTTPS_PROXY = "HTTPS_PROXY";
    static final String HTTPS_PROXY_LC = "https_proxy";
    static final String NO_PROXY = "NO_PROXY";
    static final String NO_PROXY_LC = "no_proxy";
    
    private static final String PROXY_PATTERN = "(https?)://(([^:]+)(:(.+))?@)?([\\da-zA-Z.-]+)(:(\\d+))?/?";
    
    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 443;
    
    static void configure(ClientConfiguration config) {
        if (config.getProtocol() == Protocol.HTTP) {
            configureHTTP(config);
        } else {
            configureHTTPS(config);
        }
        configureNonProxyHosts(config);
    }
    
    private static void configureNonProxyHosts(ClientConfiguration config) {
        String noProxy = getEnv(NO_PROXY, NO_PROXY_LC);
        if (noProxy != null) {
            config.setNonProxyHosts(Joiner.on('|').join(noProxy.split(",")));
        }
    }
    
    private static void configureHTTP(ClientConfiguration config) {
        String env = getEnv(HTTP_PROXY, HTTP_PROXY_LC);
        if (env != null) {
            configureProxy(config, env, HTTP_PORT);
        }
    }
    
    private static void configureHTTPS(ClientConfiguration config) {
        String env = getEnv(HTTPS_PROXY, HTTPS_PROXY_LC);
        if (env != null) {
            configureProxy(config, env, HTTPS_PORT);
        }
    }
    
    private static void configureProxy(ClientConfiguration config, String env, int defaultPort) {
        Pattern pattern = Pattern.compile(PROXY_PATTERN);
        Matcher matcher = pattern.matcher(env);
        if (matcher.matches()) {
            if (matcher.group(3) != null) {
                config.setProxyUsername(matcher.group(3));
            }
            if (matcher.group(5) != null) {
                config.setProxyPassword(matcher.group(5));
            }
            config.setProxyHost(matcher.group(6));
            if (matcher.group(8) != null) {
                config.setProxyPort(Integer.parseInt(matcher.group(8)));
            } else {
                config.setProxyPort(defaultPort);
            }
        }
    }
    
    private static String getEnv(String name, String name2) {
        String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        String value2 = System.getenv(name2);
        if (value2 != null) {
            return value2;
        }
        return null;
    }
    
    private ProxyConfiguration() {
        // do not create instances
    }
}
