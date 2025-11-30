// ===== CARRITO GLOBAL CON ANIMACIONES =====
const cart = {
    items: [],

    load() {
        const saved = localStorage.getItem('cart');
        this.items = saved ? JSON.parse(saved) : [];
        this.updateBadge();
    },

    add(product) {
        const existing = this.items.find(item => item.id === product.id);
        if (existing) {
            existing.quantity++;
        } else {
            this.items.push({ ...product, quantity: 1 });
        }
        this.save();
        this.updateBadge();
        this.showNotification(`✨ ${product.name} agregado al carrito`, 'success');
        this.animateCartBadge();
    },

    remove(id) {
        this.items = this.items.filter(item => item.id !== id);
        this.save();
        this.updateBadge();
        this.showNotification('🗑️ Producto removido', 'info');
    },

    updateQuantity(id, quantity) {
        const item = this.items.find(item => item.id === id);
        if (item) {
            if (quantity <= 0) {
                this.remove(id);
            } else {
                item.quantity = quantity;
                this.save();
                this.updateBadge();
            }
        }
    },

    save() {
        localStorage.setItem('cart', JSON.stringify(this.items));
    },

    updateBadge() {
        const badge = document.querySelector('.badge');
        const total = this.items.reduce((sum, item) => sum + item.quantity, 0);
        
        if (badge) {
            if (total > 0) {
                badge.textContent = total > 99 ? '99+' : total;
                badge.style.display = 'flex';
            } else {
                badge.style.display = 'none';
            }
        }
    },

    animateCartBadge() {
        const badge = document.querySelector('.badge');
        if (badge) {
            badge.style.animation = 'none';
            setTimeout(() => {
                badge.style.animation = 'heartbeat 0.6s ease-in-out';
            }, 10);
        }
    },

    showNotification(message, type = 'success') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        
        const bgColor = {
            'success': '#26C485',
            'error': '#EC1C24',
            'info': '#0066CC',
            'warning': '#FFA500'
        }[type] || '#0066CC';
        
        notification.style.cssText = `
            position: fixed;
            bottom: 20px;
            right: 20px;
            background: ${bgColor};
            color: white;
            padding: 14px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 10000;
            animation: slideInRight 0.3s ease, fadeOut 0.3s ease 2.7s forwards;
            font-weight: 500;
            font-size: 14px;
            max-width: 300px;
            word-wrap: break-word;
        `;
        
        notification.textContent = message;
        document.body.appendChild(notification);
        
        setTimeout(() => notification.remove(), 3000);
    }
};

// ===== INICIALIZAR AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', () => {
    cart.load();
    updateActiveNav();
    setupScrollEffects();
    setupIntersectionObserver();
    setupButtonInteractions();
    setupFormAnimations();
});

// ===== ACTUALIZAR NAVEGACIÓN ACTIVA =====
function updateActiveNav() {
    const currentPage = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        if ((currentPage === '/' && href === '/') || 
            (currentPage !== '/' && href !== '/' && currentPage.includes(href))) {
            link.classList.add('active');
        }
    });
}

// ===== EFECTOS DE SCROLL =====
function setupScrollEffects() {
    const header = document.querySelector('.header');
    if (!header) return;
    
    let lastScrollTop = 0;
    let ticking = false;

    const updateScroll = () => {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        
        if (scrollTop > 10) {
            header.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
        } else {
            header.style.boxShadow = 'none';
        }
        
        lastScrollTop = scrollTop;
        ticking = false;
    };

    window.addEventListener('scroll', () => {
        if (!ticking) {
            window.requestAnimationFrame(updateScroll);
            ticking = true;
        }
    }, { passive: true });
}

// ===== INTERSECTION OBSERVER PARA ANIMACIONES =====
function setupIntersectionObserver() {
    const options = {
        threshold: 0.1,
        rootMargin: '0px 0px -100px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                entry.target.style.animation = `fadeIn 0.5s ease ${index * 0.05}s both`;
                observer.unobserve(entry.target);
            }
        });
    }, options);

    // Observar elementos
    document.querySelectorAll('.product-card, .category-card, .benefit-item').forEach(el => {
        observer.observe(el);
    });
}

// ===== INTERACCIONES DE BOTONES CON RIPPLE EFFECT =====
function setupButtonInteractions() {
    document.querySelectorAll('.btn').forEach(button => {
        button.addEventListener('click', function(e) {
            // Ripple effect
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            
            ripple.style.cssText = `
                position: absolute;
                width: 20px;
                height: 20px;
                background: rgba(255, 255, 255, 0.5);
                border-radius: 50%;
                left: ${x}px;
                top: ${y}px;
                pointer-events: none;
                animation: ripple 0.6s ease-out;
            `;
            
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);
            
            setTimeout(() => ripple.remove(), 600);
        });

        // Hover effects
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });

        button.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
}

// ===== ANIMACIONES DE FORMULARIOS =====
function setupFormAnimations() {
    document.querySelectorAll('input, select, textarea').forEach(field => {
        field.addEventListener('focus', function() {
            this.style.transform = 'scale(1.02)';
            this.style.boxShadow = '0 0 0 3px rgba(236, 28, 36, 0.1)';
        });

        field.addEventListener('blur', function() {
            this.style.transform = 'scale(1)';
            this.style.boxShadow = 'none';
        });
    });
}

// ===== BÚSQUEDA CON PLACEHOLDER ANIMADO =====
const searchInput = document.querySelector('.search-bar input');
if (searchInput) {
    const originalPlaceholder = searchInput.placeholder;
    let placeholderIndex = 0;
    const placeholders = [
        'Buscar productos...',
        'Buscar laptops...',
        'Buscar teléfonos...',
        'Buscar accesorios...'
    ];

    // Cambiar placeholder cada 4 segundos
    setInterval(() => {
        if (document.activeElement !== searchInput) {
            placeholderIndex = (placeholderIndex + 1) % placeholders.length;
            searchInput.placeholder = placeholders[placeholderIndex];
        }
    }, 4000);

    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const query = searchInput.value.trim();
            if (query) {
                searchInput.style.opacity = '0.5';
                searchInput.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    window.location.href = `/productos?search=${encodeURIComponent(query)}`;
                }, 200);
            }
        }
    });

    searchInput.addEventListener('focus', function() {
        this.style.boxShadow = '0 2px 8px rgba(236, 28, 36, 0.2)';
    });

    searchInput.addEventListener('blur', function() {
        this.style.boxShadow = 'none';
    });
}

// ===== SMOOTH SCROLL PARA ANCHOR LINKS =====
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
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

// ===== COUNTER ANIMATION =====
function animateCounter(element, target, duration = 1000) {
    let current = 0;
    const increment = target / (duration / 16);
    const counter = setInterval(() => {
        current += increment;
        if (current >= target) {
            element.textContent = target;
            clearInterval(counter);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

// ===== PARALLAX EFFECT =====
document.addEventListener('scroll', () => {
    document.querySelectorAll('[data-parallax]').forEach(el => {
        const speed = el.getAttribute('data-parallax') || 0.5;
        el.style.transform = `translateY(${window.scrollY * speed}px)`;
    });
}, { passive: true });

// ===== KEYBOARD SHORTCUTS =====
document.addEventListener('keydown', (e) => {
    // Ctrl/Cmd + K para abrir búsqueda
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (searchInput) searchInput.focus();
    }
    
    // Esc para limpiar búsqueda
    if (e.key === 'Escape' && searchInput === document.activeElement) {
        searchInput.value = '';
        searchInput.blur();
    }
});

// ===== ANIMACIONES DE CARGA =====
window.addEventListener('load', () => {
    document.body.style.animation = 'fadeIn 0.5s ease';
});