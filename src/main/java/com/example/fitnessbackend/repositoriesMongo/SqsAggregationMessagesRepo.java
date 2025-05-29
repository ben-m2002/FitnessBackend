package com.example.fitnessbackend.repositoriesMongo;

import com.example.fitnessbackend.entities.SqsAggregationMessages;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SqsAggregationMessagesRepo extends MongoRepository<SqsAggregationMessages, String> {
    Boolean existsByMessageId(String messageId);
}
