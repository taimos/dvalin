package de.taimos.dvalin.dynamodb.marshal;

/*-
 * #%L
 * DynamoDB support for dvalin
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EnumMarshallerTest {

    @Test
    void marshall() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assertions.assertEquals(TestEnum.EnumValue1.toString(), m.marshall(TestEnum.EnumValue1));
        Assertions.assertEquals(TestEnum.ENUMVALUE2.toString(), m.marshall(TestEnum.ENUMVALUE2));
    }

    @Test
    void marshallNull() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assertions.assertNull(m.marshall(null));
    }

    @Test
    void unmarshall() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assertions.assertEquals(TestEnum.EnumValue1, m.unmarshall(TestEnum.class, TestEnum.EnumValue1.toString()));
        Assertions.assertEquals(TestEnum.ENUMVALUE2, m.unmarshall(TestEnum.class, TestEnum.ENUMVALUE2.toString()));
    }

    @Test
    void unmarshallNull() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assertions.assertNull(m.unmarshall(TestEnum.class, null));
    }

}
