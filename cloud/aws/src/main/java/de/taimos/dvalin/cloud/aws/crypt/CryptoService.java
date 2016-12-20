package de.taimos.dvalin.cloud.aws.crypt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.cloud.aws.AWSClient;

@Service
@OnSystemProperty(propertyName = "aws.kmsKeyId")
public class CryptoService {
    
    public static final String DAEMON_NAME = "DaemonName";
    @AWSClient
    private AWSKMSClient kmsClient;
    
    @Value("${aws.kmsKeyId}")
    private String kmsKeyId;
    
    public ByteBuffer encrypt(String stringToEncrypt, Map<String, String> aeadContext) {
        final EncryptRequest enc = new EncryptRequest();
        enc.setKeyId(this.kmsKeyId);
        enc.setPlaintext(ByteBuffer.wrap(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
        enc.setEncryptionContext(aeadContext);
        return this.kmsClient.encrypt(enc).getCiphertextBlob();
    }
    
    public String decrypt(ByteBuffer encryptedBuffer, Map<String, String> aeadContext) {
        final DecryptRequest dec = new DecryptRequest();
        dec.setCiphertextBlob(encryptedBuffer);
        dec.setEncryptionContext(aeadContext);
        final ByteBuffer plaintext = this.kmsClient.decrypt(dec).getPlaintext();
        return new String(plaintext.array(), StandardCharsets.UTF_8);
    }
    
    public ByteBuffer encryptWithDefaultContext(String stringToEncrypt) {
        Map<String, String> aeadContext = new HashMap<>();
        aeadContext.put(DAEMON_NAME, DaemonStarter.getDaemonName());
        return this.encrypt(stringToEncrypt, aeadContext);
    }
    
    public String decryptWithDefaultContext(ByteBuffer encryptedBuffer) {
        Map<String, String> aeadContext = new HashMap<>();
        aeadContext.put(DAEMON_NAME, DaemonStarter.getDaemonName());
        return this.decrypt(encryptedBuffer, aeadContext);
    }
    
}
