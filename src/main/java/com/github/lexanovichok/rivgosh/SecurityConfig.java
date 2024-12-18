package com.github.lexanovichok.rivgosh;

import com.github.lexanovichok.rivgosh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig  {



    // Используем SecurityFilterChain для настройки безопасности
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF, если необходимо (по умолчанию включен)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Доступ только для администраторов
                        .requestMatchers("/user/**").hasRole("USER")   // Доступ для пользователей
                        .requestMatchers("/**").permitAll()            // Открытый доступ для всех других
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")            // Указываем кастомный путь для выхода
                        .logoutSuccessUrl("/index.html") // Перенаправление после выхода
                        .permitAll()                     // Разрешаем доступ для всех
                );;
        return http.build();
    }

    // Создаем UserDetailsService для загрузки данных пользователя
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            if (username.equals("admin")) {
                return User.withUsername("admin")
                        .password(passwordEncoder().encode("admin"))
                        .roles("ADMIN")
                        .build();
            } else if (username.equals("user")) {
                return User.withUsername("user")
                        .password(passwordEncoder().encode("user"))
                        .roles("USER")
                        .build();
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean для PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}