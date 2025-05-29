package com.example.fitnessbackend.entities;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("aggregation_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SqsAggregationMessages {
    @Id
    private String messageId;

    @CreatedDate
    @Indexed(expireAfter = "4d")
    private Date timestamp;
}
