package com.example.fitnessbackend.repositories;

import com.example.fitnessbackend.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserModelRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByEmail(String email);
    UserModel findByUsername(String username);
    UserModel findById(int id);
    UserModel findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
