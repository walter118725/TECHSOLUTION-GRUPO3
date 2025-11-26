// ============================================
// TechSolutions - JavaScript Principal
// Funcionalidad para experiencia agradable
// ============================================

// Estado del carrito
let cart = JSON.parse(localStorage.getItem('cart')) || [];

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    updateCartCount();
    showWelcomeMessage();
});

// ============================================
// Gestión del Carrito
// ============================================

function updateCartCount() {
    const cartBadge = document.getElementById('cartCount');
    if (cartBadge) {
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        cartBadge.textContent = totalItems;
        
        // Animación al actualizar
        cartBadge.style.transform = 'scale(1.3)';
        setTimeout(() => {
            cartBadge.style.transform = 'scale(1)';
        }, 200);
    }
}

function addToCart(productId) {
    // Simulación de productos
    const products = {
        1: { id: 1, name: 'Laptop Gaming Pro', price: 2999, emoji: '💻' },
        2: { id: 2, name: 'Smartphone Ultra', price: 1499, emoji: '📱' },
        3: { id: 3, name: 'Audífonos Premium', price: 299, emoji: '🎧' },
        4: { id: 4, name: 'Teclado Mecánico', price: 399, emoji: '⌨️' }
    };
    
    const product = products[productId];
    
    if (!product) return;
    
    // Verificar si ya existe en el carrito
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        existingItem.quantity += 1;
        showNotification(`¡Genial! Agregamos otra unidad de ${product.name} 🎉`, 'success');
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            emoji: product.emoji,
            quantity: 1
        });
        showNotification(`¡${product.name} agregado al carrito! ${product.emoji}`, 'success');
    }
    
    // Guardar en localStorage
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
    
    // Animación del botón
    event.target.classList.add('btn-success');
    setTimeout(() => {
        event.target.classList.remove('btn-success');
    }, 1000);
}

// ============================================
// Notificaciones Amigables
// ============================================

function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '9999';
    notification.style.minWidth = '300px';
    notification.style.animation = 'fadeIn 0.3s ease-out';
    notification.innerHTML = message;
    
    document.body.appendChild(notification);
    
    // Remover después de 3 segundos
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(400px)';
        notification.style.transition = 'all 0.3s ease-out';
        
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// ============================================
// Mensaje de Bienvenida Personalizado
// ============================================

function showWelcomeMessage() {
    const hour = new Date().getHours();
    let greeting = '';
    
    if (hour < 12) {
        greeting = '¡Buenos días! ☀️';
    } else if (hour < 18) {
        greeting = '¡Buenas tardes! 🌤️';
    } else {
        greeting = '¡Buenas noches! 🌙';
    }
    
    // Mostrar solo en la página principal
    if (window.location.pathname === '/' || window.location.pathname === '/index.html') {
        setTimeout(() => {
            showNotification(`${greeting} Bienvenido a TechSolutions. ¿En qué podemos ayudarte hoy? 😊`, 'info');
        }, 500);
    }
}

// ============================================
// Interacciones Suaves
// ============================================

// Smooth scroll para enlaces internos
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Animación de hover para tarjetas
document.querySelectorAll('.product-card, .feature-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transition = 'all 0.3s ease';
    });
});

// ============================================
// Formateo de Precios
// ============================================

function formatPrice(price) {
    return `S/ ${price.toFixed(2)}`;
}

// ============================================
// Validación de Formularios Amigable
// ============================================

function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return true;
    
    const inputs = form.querySelectorAll('input[required], select[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.style.borderColor = '#ef4444';
            showNotification(`Por favor, completa el campo: ${input.previousElementSibling.textContent}`, 'warning');
        } else {
            input.style.borderColor = '#10b981';
        }
    });
    
    return isValid;
}

// ============================================
// Loading States
// ============================================

function showLoading(element) {
    if (element) {
        element.disabled = true;
        element.innerHTML = '<span>⏳ Procesando...</span>';
    }
}

function hideLoading(element, originalText) {
    if (element) {
        element.disabled = false;
        element.innerHTML = originalText;
    }
}

// ============================================
// Utilidades
// ============================================

function getCart() {
    return JSON.parse(localStorage.getItem('cart')) || [];
}

function clearCart() {
    localStorage.removeItem('cart');
    cart = [];
    updateCartCount();
}

// Exportar funciones globales
window.addToCart = addToCart;
window.showNotification = showNotification;
window.formatPrice = formatPrice;
window.validateForm = validateForm;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.getCart = getCart;
window.clearCart = clearCart;
