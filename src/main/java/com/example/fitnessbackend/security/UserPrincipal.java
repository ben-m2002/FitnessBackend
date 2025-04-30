package com.example.fitnessbackend.security;

import com.example.fitnessbackend.models.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



public class UserPrincipal implements UserDetails {
    // Pretty much the user instance that gets used in the actual application
    // UserModel is just for the database

    private final UserModel userModel;

    public UserPrincipal(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userModel.getPassword();
    }

    @Override
    public String getUsername() {
        return userModel.getPassword();
    }

    public String getEmail(){
        return userModel.getEmail();
    }
}
