package com.github.lexanovichok.rivgosh.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationService {
    private final AuthenticationManager authenticationManager;


    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
    }

    public boolean authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (BadCredentialsException e) {
            return false;
        }
    }
}
