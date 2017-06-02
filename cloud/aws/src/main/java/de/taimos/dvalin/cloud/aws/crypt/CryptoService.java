package de.taimos.dvalin.cloud.aws.crypt;

/*-
 * #%L
 * Dvalin AWS support
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
    
    private static final String DAEMON_NAME = "DaemonName";
    
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
