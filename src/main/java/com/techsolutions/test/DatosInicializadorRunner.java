package com.techsolutions.test;

import com.techsolutions.model.Categoria;
import com.techsolutions.model.Producto;
import com.techsolutions.repository.CategoriaRepository;
import com.techsolutions.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Inicializador de datos de prueba para productos
 * Crea productos de ejemplo para probar el patrón Observer
 */
@Component
@Order(4)
public class DatosInicializadorRunner implements CommandLineRunner {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Override
    public void run(String... args) {
        // Solo crear datos si no existen
        if (productoRepository.count() > 0) {
            System.out.println("✅ Productos ya existen en la base de datos");
            return;
        }
        
        System.out.println("\n📦 Inicializando datos de prueba...\n");
        
        // Crear categorías
        Categoria electronica = new Categoria();
        electronica.setNombre("Electrónica");
        electronica.setDescripcion("Productos electrónicos");
        electronica.setActiva(true);
        categoriaRepository.save(electronica);
        
        Categoria oficina = new Categoria();
        oficina.setNombre("Oficina");
        oficina.setDescripcion("Artículos de oficina");
        oficina.setActiva(true);
        categoriaRepository.save(oficina);
        
        // Crear productos
        Producto laptop = new Producto();
        laptop.setCodigo("ELEC-001");
        laptop.setNombre("Laptop HP ProBook");
        laptop.setDescripcion("Laptop para oficina, 8GB RAM, 256GB SSD");
        laptop.setPrecio(new BigDecimal("2500.00"));
        laptop.setStock(25);
        laptop.setStockMinimo(10);
        laptop.setCategoria(electronica);
        laptop.setActivo(true);
        productoRepository.save(laptop);
        
        Producto mouse = new Producto();
        mouse.setCodigo("ELEC-002");
        mouse.setNombre("Mouse Inalámbrico Logitech");
        mouse.setDescripcion("Mouse inalámbrico ergonómico");
        mouse.setPrecio(new BigDecimal("45.00"));
        mouse.setStock(8);
        mouse.setStockMinimo(15);
        mouse.setCategoria(electronica);
        mouse.setActivo(true);
        productoRepository.save(mouse);
        
        Producto teclado = new Producto();
        teclado.setCodigo("ELEC-003");
        teclado.setNombre("Teclado Mecánico RGB");
        teclado.setDescripcion("Teclado mecánico con iluminación RGB");
        teclado.setPrecio(new BigDecimal("180.00"));
        teclado.setStock(12);
        teclado.setStockMinimo(8);
        teclado.setCategoria(electronica);
        teclado.setActivo(true);
        productoRepository.save(teclado);
        
        Producto cuadernos = new Producto();
        cuadernos.setCodigo("OFIC-001");
        cuadernos.setNombre("Cuadernos A4 (Pack 10)");
        cuadernos.setDescripcion("Pack de 10 cuadernos A4 rayados");
        cuadernos.setPrecio(new BigDecimal("25.00"));
        cuadernos.setStock(50);
        cuadernos.setStockMinimo(20);
        cuadernos.setCategoria(oficina);
        cuadernos.setActivo(true);
        productoRepository.save(cuadernos);
        
        Producto lapices = new Producto();
        lapices.setCodigo("OFIC-002");
        lapices.setNombre("Lápices (Caja 24)");
        lapices.setDescripcion("Caja de 24 lápices HB");
        lapices.setPrecio(new BigDecimal("12.00"));
        lapices.setStock(5);
        lapices.setStockMinimo(10);
        lapices.setActivo(true);
        lapices.setCategoria(oficina);
        productoRepository.save(lapices);
        
        System.out.println("✅ Datos de prueba inicializados:");
        System.out.println("   • " + laptop.getNombre() + " (Stock: " + laptop.getStock() + ", Mínimo: " + laptop.getStockMinimo() + ")");
        System.out.println("   • " + mouse.getNombre() + " (Stock: " + mouse.getStock() + ", Mínimo: " + mouse.getStockMinimo() + ") ⚠️ BAJO STOCK");
        System.out.println("   • " + teclado.getNombre() + " (Stock: " + teclado.getStock() + ", Mínimo: " + teclado.getStockMinimo() + ")");
        System.out.println("   • " + cuadernos.getNombre() + " (Stock: " + cuadernos.getStock() + ", Mínimo: " + cuadernos.getStockMinimo() + ")");
        System.out.println("   • " + lapices.getNombre() + " (Stock: " + lapices.getStock() + ", Mínimo: " + lapices.getStockMinimo() + ") ⚠️ BAJO STOCK\n");
    }
}
