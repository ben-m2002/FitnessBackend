package com.example.fitnessbackend.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.example.fitnessbackend.dtos.responses.workout.SetEntryResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ExerciseAggregationHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context ctx) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            String body = msg.getBody();
            ctx.getLogger().log("BODY: " + body);

            Map<String,SQSEvent.MessageAttribute> attrs = msg.getMessageAttributes();

            String sender = attrs.get("sender").getStringValue();
            String userId = attrs.get("userId").getStringValue();

            ctx.getLogger().log("SENDER: " + sender + ", USERID: " + userId);

            try {
                SetEntryResponseDto setEntryDto = new ObjectMapper().readValue(body, SetEntryResponseDto.class);
                // Find messageId in mongoDB
                String messageId = attrs.get("messageId").getStringValue();


            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
        return null;
    }
}