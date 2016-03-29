package de.taimos.dvalin.notification.mail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import de.taimos.daemon.spring.annotations.TestComponent;

@TestComponent
public class TestMailSender implements MailSender {

    private List<SimpleMailMessage> messages = new ArrayList<>();

    @PostConstruct
    public void init(){
        System.out.println("INIT TEST MAIL SENDER");
    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        messages.add(simpleMailMessage);
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
        for (SimpleMailMessage message : simpleMailMessages) {
            send(message);
        }
    }

    public List<SimpleMailMessage> getMessages() {
        return messages;
    }

    public void clear() {
        this.messages.clear();
    }
}
