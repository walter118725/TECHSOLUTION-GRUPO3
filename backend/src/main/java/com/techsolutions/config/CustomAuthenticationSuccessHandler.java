package com.techsolutions.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Handler personalizado para redirigir a cada rol a su dashboard específico
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        String targetUrl;
        
        if (roles.contains("ROLE_ADMIN")) {
            // Admin va a su dashboard específico de administración
            targetUrl = "/admin";
        } else if (roles.contains("ROLE_GERENTE")) {
            // Gerente va directamente a su dashboard específico
            targetUrl = "/gerente";
        } else if (roles.contains("ROLE_CLIENTE")) {
            // Cliente va al catálogo de productos
            targetUrl = "/productos";
        } else {
            // Por defecto, dashboard
            targetUrl = "/dashboard";
        }
        
        response.sendRedirect(targetUrl);
    }
}
