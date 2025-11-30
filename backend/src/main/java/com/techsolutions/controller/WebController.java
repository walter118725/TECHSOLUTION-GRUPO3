package com.techsolutions.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador Web para páginas de cliente con gestión de login y permisos
 * Proporciona una experiencia amigable y agradable para los usuarios
 */
@Controller
public class WebController {

    /**
     * Página principal con bienvenida cálida
     */
    @GetMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        return "index";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }
        return "login";
    }

    /**
     * Página de registro
     */
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    /**
     * Dashboard principal después del login
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        return "dashboard";
    }

    /**
     * Catálogo completo de productos
     */
    @GetMapping("/productos")
    public String productos(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
        }
        return "productos";
    }

    /**
     * Carrito de compras del cliente (requiere autenticación)
     */
    @GetMapping("/carrito")
    public String carrito(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "carrito";
    }

    /**
     * Perfil del usuario (requiere autenticación)
     */
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        return "perfil";
    }

    /**
     * Panel de administración (solo ADMIN)
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "admin";
    }

    /**
     * Gestión de reportes (GERENTE y CONTADOR)
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "reportes";
    }

    /**
     * Gestión de inventario (ADMIN, GERENTE y COMPRAS)
     */
    @GetMapping("/inventario")
    public String inventario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "inventario";
    }

    /**
     * Gestión de ventas (ADMIN, GERENTE y VENTAS)
     */
    @GetMapping("/ventas")
    public String ventas(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "ventas";
    }
}
