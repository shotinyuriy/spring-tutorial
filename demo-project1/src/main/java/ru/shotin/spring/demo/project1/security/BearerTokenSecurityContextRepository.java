package ru.shotin.spring.demo.project1.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import ru.shotin.spring.demo.project1.security.jwt.DecodedJWT;
import ru.shotin.spring.demo.project1.security.jwt.JwtDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BearerTokenSecurityContextRepository implements SecurityContextRepository {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest httpRequest = requestResponseHolder.getRequest();
        String authZ = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authZ == null || authZ.isBlank() || !authZ.toLowerCase().startsWith("bearer ")) {
            return new SecurityContextImpl();
        }
        try {
            String bearerValue = authZ.substring("bearer ".length());
            DecodedJWT decodedJWT = jwtDecoder.decode(bearerValue);
            return new SecurityContextImpl(new BearerTokenAuthentication(decodedJWT, bearerValue, decodedJWT.authorities()));
        } catch (RuntimeException re) {
            return new SecurityContextImpl();
        }
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.setContext(context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return SecurityContextHolder.getContext() != null;
    }
}
