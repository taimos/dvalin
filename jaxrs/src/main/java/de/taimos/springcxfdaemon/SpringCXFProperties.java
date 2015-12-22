package de.taimos.springcxfdaemon;

public interface SpringCXFProperties {
	
	/**
	 * the port to bind to <br>
	 * <strong>default:</strong> 8080
	 */
	public static final String JAXRS_BINDPORT = "jaxrs.bindport";
	
	/**
	 * the host to bind to <br>
	 * <strong>default:</strong> 0.0.0.0
	 */
	public static final String JAXRS_BINDHOST = "jaxrs.bindhost";
	
	/**
	 * the base path of the JAX-RS endpoint <br>
	 * <strong>default:</strong>
	 */
	public static final String JAXRS_PATH = "jaxrs.path";
	
	/**
	 * the service annotation <br>
	 * <strong>default:</strong> de.taimos.springcxfdaemon.JaxRsComponent
	 */
	public static final String JAXRS_ANNOTATION = "jaxrs.annotation";
	
	/**
	 * send the jetty version as Response Header <br>
	 * <strong>default:</strong> false
	 */
	public static final String JETTY_SENDVERSION = "jetty.sendVersion";
	
	/**
	 * minimum number of jetty threads <br>
	 * <strong>default:</strong> 5
	 */
	public static final String JETTY_MINTHREADS = "jetty.minThreads";
	
	/**
	 * maximum number of jetty threads <br>
	 * <strong>default:</strong> 150
	 */
	public static final String JETTY_MAXTHREADS = "jetty.MaxThreads";
	
	/**
	 * use Jetty sessions <br>
	 * <strong>default:</strong> false
	 */
	public static final String JETTY_SESSIONS = "jetty.sessions";
	
	/**
	 * the public base URL of the server<br>
	 * <strong>default:</strong> http://localhost:&lt;port&gt;
	 */
	public static final String SERVER_URL = "server.url";
	
	/**
	 * the class to use as default handler <br>
	 * <strong>default:</strong> org.eclipse.jetty.server.handler.DefaultHandler
	 */
	public static final String DEFAULT_HANDLER = "defaultHandlerClass";
	
}
