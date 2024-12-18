package com.github.lexanovichok.rivgosh.services;

import com.github.lexanovichok.rivgosh.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                // Теперь передаем роль как строку
                .roles(user.getRole())
                .build();
    }


    public boolean registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        userRepository.save(user);
        return true;
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Проверяем пароль
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Создаем объект аутентификации с ролью
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + user.getRole());
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        }
        return false;
    }

}