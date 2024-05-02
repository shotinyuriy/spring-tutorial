package ru.shotin.spring.demo.project1.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class BearerTokenAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof BearerTokenAuthentication bearerTokenAuthentication) {
            return authentication;
        }
        throw new AccessDeniedException("Bearer Token Has Not Been Provided");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.equals(authentication);
    }
}
