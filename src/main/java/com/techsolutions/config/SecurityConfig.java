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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Usuario Administrador - Acceso total
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN", "GERENTE", "CONTADOR")
            .build();

        // Usuario Gerente - Acceso a reportes y gestión
        UserDetails gerente = User.builder()
            .username("gerente")
            .password(passwordEncoder.encode("gerente123"))
            .roles("GERENTE")
            .build();

        // Usuario Contador - Acceso a reportes financieros
        UserDetails contador = User.builder()
            .username("contador")
            .password(passwordEncoder.encode("contador123"))
            .roles("CONTADOR")
            .build();

        // Usuario Ventas - Acceso limitado a ventas
        UserDetails ventas = User.builder()
            .username("ventas")
            .password(passwordEncoder.encode("ventas123"))
            .roles("VENTAS")
            .build();

        // Usuario Compras - Acceso a inventario y compras
        UserDetails compras = User.builder()
            .username("compras")
            .password(passwordEncoder.encode("compras123"))
            .roles("COMPRAS")
            .build();

        // Usuario Cliente - Acceso público
        UserDetails cliente = User.builder()
            .username("cliente")
            .password(passwordEncoder.encode("cliente123"))
            .roles("CLIENTE")
            .build();

        return new InMemoryUserDetailsManager(admin, gerente, contador, ventas, compras, cliente);
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
                
                // Catálogo de productos - acceso público
                .requestMatchers("/productos").permitAll()
                
                // Dashboard y perfil - requiere autenticación
                .requestMatchers("/dashboard", "/carrito", "/perfil").authenticated()
                
                // Panel de administración - solo ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Gestión de reportes - GERENTE y CONTADOR
                .requestMatchers("/reportes/**").hasAnyRole("ADMIN", "GERENTE", "CONTADOR")
                
                // Gestión de inventario - ADMIN, GERENTE y COMPRAS
                .requestMatchers("/inventario/**").hasAnyRole("ADMIN", "GERENTE", "COMPRAS")
                
                // Gestión de ventas - ADMIN, GERENTE y VENTAS
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "GERENTE", "VENTAS")
                
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
                .defaultSuccessUrl("/dashboard", true)
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
