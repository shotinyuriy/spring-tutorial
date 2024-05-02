package ru.shotin.spring.demo.project1.security.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtDecoder {
    public DecodedJWT decode(String encodedJwt) {
        return new DecodedJWT();
    }
}
