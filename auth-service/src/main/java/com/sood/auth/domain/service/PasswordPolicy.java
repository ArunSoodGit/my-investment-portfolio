package com.sood.auth.domain.service;


import com.sood.auth.domain.model.User;

public interface PasswordPolicy {
    void validate(String password);

    boolean validateLogin(User user, String rawPassword);

    String encodePassword(String rawPassword) ;
}
