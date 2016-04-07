package de.taimos.dvalin.dynamodb.marshal;

import org.junit.Assert;
import org.junit.Test;

public class EnumMarshallerTest {

    @Test
    public void marshall() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assert.assertEquals(TestEnum.EnumValue1.toString(), m.marshall(TestEnum.EnumValue1));
        Assert.assertEquals(TestEnum.ENUMVALUE2.toString(), m.marshall(TestEnum.ENUMVALUE2));
    }

    @Test
    public void marshallNull() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assert.assertNull(m.marshall(null));
    }

    @Test
    public void unmarshall() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assert.assertEquals(TestEnum.EnumValue1, m.unmarshall(TestEnum.class, TestEnum.EnumValue1.toString()));
        Assert.assertEquals(TestEnum.ENUMVALUE2, m.unmarshall(TestEnum.class, TestEnum.ENUMVALUE2.toString()));
    }

    @Test
    public void unmarshallNull() throws Exception {
        EnumMarshaller m = new EnumMarshaller();
        Assert.assertNull(m.unmarshall(TestEnum.class, null));
    }

}
