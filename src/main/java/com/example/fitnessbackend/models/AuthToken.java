package com.example.fitnessbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true, length = 1024)
    private String token;
    private Date expirationMS;
    @Column(unique = true)
    private String email;
}
