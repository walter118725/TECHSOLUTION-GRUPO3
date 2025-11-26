package com.techsolutions.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador Web para páginas de cliente
 * Proporciona una experiencia amigable y agradable para los usuarios
 */
@Controller
public class WebController {

    /**
     * Página principal con bienvenida cálida
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Catálogo completo de productos
     */
    @GetMapping("/productos")
    public String productos() {
        return "productos";
    }

    /**
     * Carrito de compras del cliente
     */
    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }

    /**
     * Perfil del usuario
     */
    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }
}
