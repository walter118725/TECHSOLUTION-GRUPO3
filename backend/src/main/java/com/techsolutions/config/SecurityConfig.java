package com.techsolutions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Seguridad con Sistema de Login y Permisos
 * Gestiona autenticación, autorización y control de acceso por roles
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Usuario Administrador - Acceso total al sistema
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();

        // Usuario Gerente - Acceso a gestión y reportes
        UserDetails gerente = User.builder()
            .username("gerente")
            .password(passwordEncoder.encode("gerente123"))
            .roles("GERENTE")
            .build();

        // Usuario Cliente - Acceso básico
        UserDetails cliente = User.builder()
            .username("cliente")
            .password(passwordEncoder.encode("cliente123"))
            .roles("CLIENTE")
            .build();

        return new InMemoryUserDetailsManager(admin, gerente, cliente);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
            )
            .authorizeHttpRequests(auth -> auth
                // Páginas públicas
                .requestMatchers("/", "/index", "/login", "/registro", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // Página de test para desarrollo
                .requestMatchers("/admin-test").permitAll()
                
                // Catálogo de productos - acceso público
                .requestMatchers("/productos").permitAll()
                
                // Dashboard y perfil - requiere autenticación
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers("/carrito", "/perfil").hasRole("CLIENTE")
                
                // Panel de administración - solo ADMIN
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                
                // Dashboard del Gerente - solo GERENTE
                .requestMatchers("/gerente/**").hasRole("GERENTE")
                
                // Gestión de reportes - solo GERENTE
                .requestMatchers("/reportes/**").hasRole("GERENTE")
                
                // Gestión de inventario - solo GERENTE
                .requestMatchers("/inventario/**").hasRole("GERENTE")
                
                // Gestión de ventas - solo GERENTE
                .requestMatchers("/ventas/**").hasRole("GERENTE")
                
                // APIs públicas para demostración de patrones
                .requestMatchers("/api/pagos/**").permitAll()
                .requestMatchers("/api/reportes/**").permitAll()
                .requestMatchers("/api/inventario/**").permitAll()
                
                // Consola H2
                .requestMatchers("/h2-console/**").permitAll()
                
                // Resto requiere autenticación
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
