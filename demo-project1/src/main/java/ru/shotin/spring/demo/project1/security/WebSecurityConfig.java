package ru.shotin.spring.demo.project1.security;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.PlainJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    //    @Bean
//    public SecurityContextRepository bearerTokenSecurityContextRepository() {
//        log.info("bearerTokenSecurityContextRepository()");
//        return new CustomBearerTokenSecurityContextRepository();
//    }
//
    @Bean
    public JwtDecoder jwtDecoder() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair rsaKeyPair = generator.generateKeyPair();
        PublicKey publicKey = rsaKeyPair.getPublic();
        if (publicKey instanceof RSAPublicKey rsaPublicKey) {
            return NimbusJwtDecoder.withPublicKey(rsaPublicKey)
                    .jwtProcessorCustomizer(jwtCustomizer -> {
                        jwtCustomizer.setJWSKeySelector(new JWSKeySelector<SecurityContext>() {
                            @Override
                            public List<? extends Key> selectJWSKeys(JWSHeader jwsHeader, SecurityContext securityContext) throws KeySourceException {
                                log.info("selectJWSKeys() jwsHeader={}", jwsHeader);
                                return List.of(rsaPublicKey);
                            }
                        });
                    }).build();
        } else {
            throw new IllegalStateException("publicKey is not RSA. publicKey.class=" + publicKey.getClass());
        }
    }
//
//    @Bean
//    public AuthenticationProvider bearerTokenAuthenticationProvider(JwtDecoder jwtDecoder) {
//        log.info("bearerTokenAuthenticationProvider()");
//        return new JwtAuthenticationProvider(jwtDecoder);
//    }
//
//    @Bean
//    public AuthenticationManager jwtAuthenticationManager(AuthenticationProvider bearerTokenAuthenticationProvider) {
//        log.info("jwtAuthenticationManager()");
//        return new ProviderManager(bearerTokenAuthenticationProvider);
//    }
//
//    @Bean
//    public GenericFilterBean jwtValidationFilter(AuthenticationManager jwtAuthenticationManager) {
//        log.info("jwtValidationFilter()");
//        return new BearerTokenAuthenticationFilter(jwtAuthenticationManager);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        log.info("securityFilterChain()");

        http
                .authorizeRequests(authConfig -> authConfig
                        .antMatchers("/api/currency/v1/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt().decoder(jwtDecoder).jwtAuthenticationConverter(jwt -> {
                    log.info("jwt={}", jwt);
                    return new JwtAuthenticationToken(jwt, List.of());
                }))
                // Possibly more configuration ...
                .formLogin().disable() // enable form based log in
                // set permitAll for all URLs associated with Form Login
                .httpBasic().disable();
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .passwordEncoder(pwd -> Base64.getEncoder().encodeToString(pwd.getBytes(StandardCharsets.UTF_8)))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("password")
//                .passwordEncoder(pwd -> Base64.getEncoder().encodeToString(pwd.getBytes(StandardCharsets.UTF_8)))
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }
}
