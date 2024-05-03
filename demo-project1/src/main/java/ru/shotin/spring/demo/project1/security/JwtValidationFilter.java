package ru.shotin.spring.demo.project1.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class JwtValidationFilter extends GenericFilterBean implements OrderedFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpRequest) {
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("doFilter() authHeader={}", authHeader);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
