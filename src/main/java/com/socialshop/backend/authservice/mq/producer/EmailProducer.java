package com.socialshop.backend.authservice.mq.producer;

import com.socialshop.backend.authservice.constants.KafkaTopicConstant;
import com.socialshop.backend.authservice.mq.events.EmailSendEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class EmailProducer {
    private final KafkaTemplate<String, EmailSendEvent> emailSendEventKafkaTemplate;

    public void sendEmailEvent(String email, String code) {
        var event = MessageBuilder
                .withPayload(EmailSendEvent.builder().email(email).content("Your code verification is " + code).build())
                .setHeader(KafkaHeaders.TOPIC, KafkaTopicConstant.emailOtpRegisterVerification)
                .build();

        emailSendEventKafkaTemplate.send(event);
    }
}
