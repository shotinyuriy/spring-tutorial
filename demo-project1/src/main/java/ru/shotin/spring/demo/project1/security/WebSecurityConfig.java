package ru.shotin.spring.demo.project1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityContextRepository bearerTokenSecurityContextRepository() {
        return new BearerTokenSecurityContextRepository();
    }

    @Bean
    public AuthenticationProvider bearerTokenAuthenticationProvider() {
        return new BearerTokenAuthenticationProvider();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityContextRepository bearerTokenSecurityRepository,
                                                   AuthenticationProvider bearerTokenAuthenticationProvider) throws Exception {
        http
                .securityContext().securityContextRepository(bearerTokenSecurityRepository)
                .and()
                .authenticationProvider(bearerTokenAuthenticationProvider)
                .authorizeRequests()
                .antMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
                .and()
                // Possibly more configuration ...
                .formLogin().disable() // enable form based log in
                // set permitAll for all URLs associated with Form Login
                .httpBasic().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .passwordEncoder(pwd -> Base64.getEncoder().encodeToString(pwd.getBytes(StandardCharsets.UTF_8)))
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .passwordEncoder(pwd -> Base64.getEncoder().encodeToString(pwd.getBytes(StandardCharsets.UTF_8)))
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
