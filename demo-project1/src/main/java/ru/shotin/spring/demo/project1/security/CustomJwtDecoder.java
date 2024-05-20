package ru.shotin.spring.demo.project1.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

public class CustomJwtDecoder implements JwtDecoder {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            String[] base64Parts = token.split("\\.");
            if (base64Parts.length != 3) {
                throw new JwtException("Token must have 3 parts");
            }
            byte[] base64Header;
            byte[] base64Claims;
            byte[] base64Signature;
            base64Header = Base64.getUrlDecoder().decode(base64Parts[0]);
            base64Claims = Base64.getUrlDecoder().decode(base64Parts[1]);
            base64Signature = Base64.getUrlDecoder().decode(base64Parts[2]);
            if (base64Signature == null || base64Signature.length < 1) {
                throw new IllegalArgumentException("JWT digital signature is required");
            }
            Map<String, Object> jwtHeaders = objectMapper.readValue(base64Header, Map.class);
            Map<String, Object> jwtClaims = objectMapper.readValue(base64Claims, Map.class);
            Object iat = jwtClaims.get(JwtClaimNames.IAT);
            Object exp = jwtClaims.get(JwtClaimNames.EXP);

            Jwt jwt = new Jwt(token, convertToInstant(iat), convertToInstant(exp), jwtHeaders, jwtClaims);
            return jwt;
        } catch (JsonMappingException e) {
            throw new JwtException(e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new JwtException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new JwtException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JwtException(e.getMessage(), e);
        }
    }

    private Instant convertToInstant(Object value) {
        if (value instanceof Number number) {
            return Instant.ofEpochSecond(number.longValue());
        } else if (value instanceof String string) {
            return Instant.ofEpochSecond(Long.parseLong(string));
        }
        throw new JwtException("Can not convert value " + value + " as Instant");
    }
}
