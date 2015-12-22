/**
 * 
 */
package de.taimos.springcxfdaemon;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.springcxfdaemon.URLUtils.SplitURL;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
public class URLTest {
	
	@Test
	public void testURLSplit() {
		this.assertSplit("http://localhost:8080", "http", "localhost", "8080", "/");
		this.assertSplit("https://localhost:8080", "https", "localhost", "8080", "/");
		this.assertSplit("http://127.0.0.1:8080", "http", "127.0.0.1", "8080", "/");
		this.assertSplit("http://localhost", "http", "localhost", "80", "/");
		this.assertSplit("http://127.0.0.1", "http", "127.0.0.1", "80", "/");
		this.assertSplit("https://127.0.0.1", "https", "127.0.0.1", "443", "/");
		this.assertSplit("http://www.foo.bar", "http", "www.foo.bar", "80", "/");
		this.assertSplit("http://www.foo.bar/blubb", "http", "www.foo.bar", "80", "/blubb");
		
		this.assertSplit("localhost", "http", "localhost", "80", "/");
		this.assertSplit("localhost:8080", "http", "localhost", "8080", "/");
		this.assertSplit("127.0.0.1", "http", "127.0.0.1", "80", "/");
		this.assertSplit("127.0.0.1:8080", "http", "127.0.0.1", "8080", "/");
		
		// this.assertSplit("", "", "", "", "");
	}
	
	private void assertSplit(String url, String scheme, String host, String port, String path) {
		SplitURL split = URLUtils.splitURL(url);
		Assert.assertEquals(scheme, split.getScheme());
		Assert.assertEquals(host, split.getHost());
		Assert.assertEquals(port, split.getPort());
		Assert.assertEquals(path, split.getPath());
	}
	
}
