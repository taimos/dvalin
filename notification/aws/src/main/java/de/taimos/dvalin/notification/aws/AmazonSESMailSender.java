/*
 * Copyright (c) 2016. Taimos GmbH
 *
 */

package de.taimos.dvalin.notification.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.taimos.dvalin.cloud.aws.AWSClient;

@Service
public class AmazonSESMailSender implements MailSender {

    public static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    @AWSClient(region = "${aws.mailregion:}")
    private AmazonSimpleEmailServiceClient sesClient;

    @Override
    public void send(SimpleMailMessage message) throws MailException {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(message.getTo());
        Preconditions.checkNotNull(message.getFrom());
        Preconditions.checkNotNull(message.getSubject());
        Preconditions.checkNotNull(message.getText());

        SendEmailRequest req = new SendEmailRequest();
        req.setSource(message.getFrom());
        if (message.getReplyTo() != null && !message.getReplyTo().isEmpty()) {
            req.setReplyToAddresses(Lists.newArrayList(message.getReplyTo()));
        }
        Destination dest = new Destination();
        dest.withToAddresses(message.getTo());
        if (message.getCc() != null) {
            dest.withCcAddresses(message.getCc());
        }
        if (message.getBcc() != null) {
            dest.withBccAddresses(message.getBcc());
        }
        req.setDestination(dest);
        Body sesBody = new Body().withHtml(new Content(message.getText()));
        Content subject = new Content(message.getSubject());
        req.setMessage(new Message().withSubject(subject).withBody(sesBody));
        SendEmailResult emailResult = sesClient.sendEmail(req);
        LOGGER.info("Sent mail over SES with message id {}", emailResult.getMessageId());
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
        for (SimpleMailMessage message : simpleMailMessages) {
            send(message);
        }
    }
}
