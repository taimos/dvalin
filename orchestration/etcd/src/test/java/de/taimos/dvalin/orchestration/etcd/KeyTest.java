package de.taimos.dvalin.orchestration.etcd;

/*-
 * #%L
 * Dvalin service orchestration with etcd
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeyTest {

    @Test
    public void shouldMatchPattern() throws Exception {
        Pattern keyPattern = Pattern.compile("/dvalin/discovery/testservice/([A-Fa-f0-9\\-]+)");

        Matcher matcher = keyPattern.matcher("/dvalin/discovery/testservice/AE7AB5B7-E6FC-46E3-BF62-EC2D25908DF6");
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("AE7AB5B7-E6FC-46E3-BF62-EC2D25908DF6", matcher.group(1));
    }
}
