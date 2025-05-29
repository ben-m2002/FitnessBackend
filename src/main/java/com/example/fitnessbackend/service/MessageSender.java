package com.example.fitnessbackend.service;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;


@org.springframework.stereotype.Service
public class MessageSender extends Service {
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper json;

    public MessageSender(JwtTokenProvider jwtTokenProvider, AuthTokenRepository authTokenRepository, AuthenticationManager authenticationManager, QueueMessagingTemplate queueMessagingTemplate, ObjectMapper json) {
        super(jwtTokenProvider, authTokenRepository, authenticationManager);
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.json = json;
    }


    public void sendMessage(Object o, String queueUrl) throws JsonProcessingException {
        String body = json.writeValueAsString(o);
        Message<String> payload = MessageBuilder
                .withPayload(body)
                .setHeader("sender", "fitness-backend")
                .setHeader("userId", getAuthenticatedUserModel().getId())
                .build();
        queueMessagingTemplate.send(queueUrl, payload);
    }
}
