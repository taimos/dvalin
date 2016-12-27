package de.taimos.dvalin.cloud.aws;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;

import de.taimos.dvalin.cloud.aws.crypt.CryptoService;

@RunWith(MockitoJUnitRunner.class)
public class CryptoServiceTest {
    
    private static final String KMS_KEY_ID = "KMSKeyId";
    
    @Mock
    private AWSKMSClient awskmsClient;
    
    private CryptoService service;
    
    @Before
    public void setUp() throws Exception {
        this.service = new CryptoService();
        
        Field ec2Field = CryptoService.class.getDeclaredField("kmsClient");
        ec2Field.setAccessible(true);
        ec2Field.set(this.service, this.awskmsClient);
        
        Field keyField = CryptoService.class.getDeclaredField("kmsKeyId");
        keyField.setAccessible(true);
        keyField.set(this.service, KMS_KEY_ID);
    }
    
    @Test
    public void shouldEncryptStringWithContext() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap("foobar".getBytes("UTF-8"));
        String stringToEncrypt = "toEncrypt";
        
        EncryptResult result = new EncryptResult().withKeyId(KMS_KEY_ID).withCiphertextBlob(byteBuffer);
        Mockito.when(this.awskmsClient.encrypt(Mockito.any())).thenReturn(result);
        
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        ByteBuffer buffer = this.service.encrypt(stringToEncrypt, map);
        
        ArgumentCaptor<EncryptRequest> captor = ArgumentCaptor.forClass(EncryptRequest.class);
        Mockito.verify(this.awskmsClient).encrypt(captor.capture());
        EncryptRequest request = captor.getValue();
        Assert.assertEquals(KMS_KEY_ID, request.getKeyId());
        Assert.assertArrayEquals(stringToEncrypt.getBytes("UTF-8"), request.getPlaintext().array());
        Assert.assertEquals(1, request.getEncryptionContext().size());
        Assert.assertEquals("value", request.getEncryptionContext().get("key"));
        Assert.assertEquals(byteBuffer, buffer);
    }
}
