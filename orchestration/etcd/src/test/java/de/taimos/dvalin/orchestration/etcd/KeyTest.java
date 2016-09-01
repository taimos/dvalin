package de.taimos.dvalin.orchestration.etcd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class KeyTest {
    
    @Test
    public void shouldMatchPattern() throws Exception {
        Pattern keyPattern = Pattern.compile("/dvalin/discovery/testservice/([A-Fa-f0-9\\-]+)");
    
        Matcher matcher = keyPattern.matcher("/dvalin/discovery/testservice/AE7AB5B7-E6FC-46E3-BF62-EC2D25908DF6");
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("AE7AB5B7-E6FC-46E3-BF62-EC2D25908DF6", matcher.group(1));
    }
}
