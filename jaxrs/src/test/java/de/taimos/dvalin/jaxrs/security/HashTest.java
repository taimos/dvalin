/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security;

/*-
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashTest {

    @Test
    void testHash() {
        String password = "test1234";
        HashedPassword hash = new HashedPassword(password);
        System.out.println(hash.getHash());
        System.out.println(hash.getRoundOffset());
        System.out.println(hash.getSalt());

        Assertions.assertTrue(hash.validate(password));

        Assertions.assertTrue(
            new HashedPassword(hash.getRoundOffset(), hash.getHash(), hash.getSalt()).validate(password));
    }

    @Test
    void stopwatch() {
        String password = "test1234test1234";
        HashedPassword hns = new HashedPassword(password);
        System.out.println(hns.getSalt());
        System.out.println(hns.getRoundOffset());
        System.out.println(hns.getHash());
        Stopwatch w = Stopwatch.createUnstarted();
        w.start();
        boolean validate = hns.validate(password);
        w.stop();
        System.out.println(w);
        Assertions.assertTrue(validate);
    }

}
