/**
 *
 */
package de.taimos.dvalin.jaxrs;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Assert;
import org.junit.Test;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
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
        URLUtils.SplitURL split = URLUtils.splitURL(url);
        Assert.assertEquals(scheme, split.getScheme());
        Assert.assertEquals(host, split.getHost());
        Assert.assertEquals(port, split.getPort());
        Assert.assertEquals(path, split.getPath());
    }

}
