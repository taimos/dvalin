package de.taimos.dvalin.cluster.hazelcast;

/*-
 * #%L
 * Dvalin Hazelcast support
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

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

@RunWith(MockitoJUnitRunner.class)
public class OptionalSerializerTest {

    @Test
    public void writeString() throws Exception {
        String string = "String";

        ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);

        new OptionalSerializer().write(output, Optional.of(string));
        Mockito.verify(output, Mockito.times(1)).writeObject(string);
    }


    @Test
    public void writeEmpty() throws Exception {
        ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);

        new OptionalSerializer().write(output, Optional.empty());
        Mockito.verify(output, Mockito.times(1)).writeObject(null);
    }


    @Test
    public void readString() throws Exception {
        String test = "Test";

        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readObject()).thenReturn(test);
        Optional read = new OptionalSerializer().read(input);
        Assert.assertTrue(read.isPresent());
        Assert.assertEquals(test, read.get());
    }


    @Test
    public void readEmpty() throws Exception {
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readObject()).thenReturn(null);
        Optional read = new OptionalSerializer().read(input);
        Assert.assertFalse(read.isPresent());
    }


    @Test
    public void getTypeId() throws Exception {
        Assert.assertEquals(1, new OptionalSerializer().getTypeId());
    }


    @Test
    public void createConfig() throws Exception {
        SerializerConfig config = OptionalSerializer.createConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals(Optional.class, config.getTypeClass());
        Assert.assertEquals(OptionalSerializer.class.getCanonicalName(), config.getClassName());
    }

}
