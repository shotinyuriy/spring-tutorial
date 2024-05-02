package ru.shotin.spring.demo.project1.security.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class DecodedJWT {
    public List<GrantedAuthority> authorities() {
        return List.of((GrantedAuthority) () -> "SYSTEM_AS_CONSUMER");
    }
}
