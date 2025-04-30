package com.example.fitnessbackend.models;

import com.example.fitnessbackend.nonPersistData.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String email;
    private String password;
    private String username;
    private String firstName;
    private UserRole role;


}
