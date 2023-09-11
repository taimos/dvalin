package de.taimos.dvalin.notification.mail;

/*-
 * #%L
 * Dvalin notification service
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

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import de.taimos.daemon.spring.annotations.TestComponent;

@TestComponent
public class TestMailSender implements MailSender {

    private final List<SimpleMailMessage> messages = new ArrayList<>();

    @PostConstruct
    public void init(){
        System.out.println("INIT TEST MAIL SENDER");
    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        this.messages.add(simpleMailMessage);
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
        for (SimpleMailMessage message : simpleMailMessages) {
            this.send(message);
        }
    }

    public List<SimpleMailMessage> getMessages() {
        return this.messages;
    }

    public void clear() {
        this.messages.clear();
    }
}
