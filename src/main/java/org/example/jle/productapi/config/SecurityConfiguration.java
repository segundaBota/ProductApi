package org.example.jle.productapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.example.jle.productapi.config.Constants.ROLE_ADMIN;
import static org.example.jle.productapi.config.Constants.ROLE_USER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(proxyTargetClass = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.user.name}") String userName,
            @Value("${app.security.user.password}") String userPassword,
            @Value("${app.security.admin.name}") String adminName,
            @Value("${app.security.admin.password}") String adminPassword) {

        UserDetails user = User.builder()
            .username(userName)
            .password(passwordEncoder.encode(userPassword))
            .roles(ROLE_USER)
            .build();

        UserDetails admin = User.builder()
            .username(adminName)
            .password(passwordEncoder.encode(adminPassword))
            .roles(ROLE_USER, ROLE_ADMIN)
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}