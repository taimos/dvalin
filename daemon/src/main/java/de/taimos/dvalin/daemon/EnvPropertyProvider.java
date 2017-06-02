package de.taimos.dvalin.daemon;

import java.util.Map;
import java.util.stream.Collectors;

import de.taimos.daemon.properties.IPropertyProvider;

public class EnvPropertyProvider implements IPropertyProvider {
	
	private static final String ENV_PREFIX = "DVALIN_";
	
	@Override
	public Map<String, String> loadProperties() {
	    return System.getenv().entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith(ENV_PREFIX))
            .collect(Collectors.toMap(e -> this.generateKey(e.getKey()), Map.Entry::getValue));
	}
	
	private String generateKey(String key) {
		return key.substring(ENV_PREFIX.length()).replace("_", ".").toLowerCase();
	}
    
    /**
     * @return true if any environment variable for Dvalin is present
     */
    public static boolean isConfigured() {
	    return System.getenv().keySet().stream().anyMatch(s -> s.startsWith(ENV_PREFIX));
	}
	
}
