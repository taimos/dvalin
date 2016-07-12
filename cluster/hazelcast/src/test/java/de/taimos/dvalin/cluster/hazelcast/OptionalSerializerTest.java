package de.taimos.dvalin.cluster.hazelcast;

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
