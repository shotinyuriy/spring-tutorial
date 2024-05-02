package ru.shotin.spring.demo.project1.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.shotin.spring.demo.project1.security.jwt.DecodedJWT;

import java.util.Collection;
import java.util.Collections;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {

    public static BearerTokenAuthentication empty() {
        return new BearerTokenAuthentication(null, null, Collections.emptyList());
    }

    private final DecodedJWT principal;
    private final String credentials;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public BearerTokenAuthentication(DecodedJWT principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
