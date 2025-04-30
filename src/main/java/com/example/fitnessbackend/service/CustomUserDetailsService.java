package com.example.fitnessbackend.service;

import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.UserModelRepository;
import com.example.fitnessbackend.security.UserPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserModelRepository userModelRepository;

    public CustomUserDetailsService(UserModelRepository userModelRepository) {
        this.userModelRepository = userModelRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String  email) throws UsernameNotFoundException {
        UserModel userModel = userModelRepository.findByEmail(email);
        if (userModel == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new UserPrincipal(userModel);
    }
}
