// ===== PRODUCTOS DESTACADOS CON EMOJIS Y DESCUENTOS =====
const sampleProducts = [
    {
        id: 1,
        name: 'MacBook Pro 16" M3',
        price: 3999,
        oldPrice: 4399,
        category: 'computadoras',
        discount: 9,
        rating: '⭐⭐⭐⭐⭐ (234)',
        emoji: '💻'
    },
    {
        id: 2,
        name: 'iPhone 15 Pro Max',
        price: 1299,
        oldPrice: 1499,
        category: 'smartphones',
        discount: 13,
        rating: '⭐⭐⭐⭐⭐ (512)',
        emoji: '📱'
    },
    {
        id: 3,
        name: 'Sony WH-1000XM5',
        price: 399,
        oldPrice: 499,
        category: 'audio',
        discount: 20,
        rating: '⭐⭐⭐⭐⭐ (189)',
        emoji: '🎧'
    },
    {
        id: 4,
        name: 'PS5 Console + Control',
        price: 599,
        oldPrice: 699,
        category: 'gaming',
        discount: 14,
        rating: '⭐⭐⭐⭐⭐ (426)',
        emoji: '🎮'
    },
    {
        id: 5,
        name: 'Apple Watch Series 9',
        price: 399,
        oldPrice: 499,
        category: 'wearables',
        discount: 20,
        rating: '⭐⭐⭐⭐⭐ (312)',
        emoji: '⌚'
    },
    {
        id: 6,
        name: 'AirPods Pro (2nd Gen)',
        price: 249,
        oldPrice: 349,
        category: 'accesorios',
        discount: 29,
        rating: '⭐⭐⭐⭐⭐ (445)',
        emoji: '🎵'
    }
];

// ===== RENDERIZAR PRODUCTOS DESTACADOS CON ANIMACIONES =====
function renderFeaturedProducts() {
    const containers = [
        document.querySelector('.products-carousel'),
        document.getElementById('featuredProducts')
    ];
    
    const container = containers.find(c => c);
    if (!container) return;
    
    container.innerHTML = sampleProducts.map((product, index) => `
        <div class="product-card" style="animation-delay: ${index * 0.1}s">
            <div class="product-image">
                <span style="font-size: 60px; display: block; animation: float 3s ease-in-out infinite;">
                    ${product.emoji}
                </span>
                <span class="product-badge">${product.discount}% OFF</span>
            </div>
            <div class="product-content">
                <div class="product-brand">${product.category.toUpperCase()}</div>
                <h3 class="product-name">${product.name}</h3>
                <div class="product-price">
                    <span class="product-price-current">S/. ${product.price}</span>
                    <span class="product-price-original">S/. ${product.oldPrice}</span>
                </div>
                <div class="product-rating">${product.rating}</div>
                <div class="product-actions">
                    <button class="btn btn-primary btn-sm" onclick="
                        cart.add({
                            id: ${product.id},
                            name: '${product.name}',
                            price: ${product.price},
                            emoji: '${product.emoji}'
                        });
                        event.target.style.transform = 'scale(0.95)';
                        setTimeout(() => { event.target.style.transform = 'scale(1)'; }, 200);
                    " style="flex: 1; transition: all 0.2s ease;">
                        🛒 Agregar
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// ===== FORMULARIO DE NEWSLETTER CON VALIDACIÓN =====
function setupNewsletterForm() {
    const form = document.querySelector('.newsletter-form');
    if (!form) return;
    
    const input = form.querySelector('input');
    const button = form.querySelector('button');
    
    if (!button || !input) return;
    
    const handleSubscribe = () => {
        const email = input.value.trim();
        
        if (!email) {
            cart.showNotification('Por favor, ingresa tu correo', 'warning');
            input.style.animation = 'pulse 0.5s ease';
            setTimeout(() => { input.style.animation = 'none'; }, 500);
            return;
        }
        
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            cart.showNotification('Correo inválido', 'error');
            input.style.animation = 'pulse 0.5s ease';
            setTimeout(() => { input.style.animation = 'none'; }, 500);
            return;
        }
        
        // Animación de procesamiento
        button.disabled = true;
        button.style.opacity = '0.7';
        button.textContent = 'Procesando...';
        
        setTimeout(() => {
            cart.showNotification('✅ Suscripción exitosa. Revisa tu correo.', 'success');
            input.value = '';
            button.disabled = false;
            button.style.opacity = '1';
            button.textContent = '📧 Suscribirse';
        }, 600);
    };
    
    button.addEventListener('click', (e) => {
        e.preventDefault();
        handleSubscribe();
    });

    input.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSubscribe();
        }
    });
}

// ===== ANIMACIONES DE CATEGORÍAS =====
function setupCategoryAnimations() {
    document.querySelectorAll('.category-item').forEach((item, index) => {
        item.style.animation = `slideInLeft 0.5s ease ${index * 0.1}s both`;
        
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-4px) scale(1.05)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

// ===== CONTADOR ANIMADO DE ESTADÍSTICAS =====
function animateCounter(element, target, duration = 1500) {
    let current = 0;
    const increment = target / (duration / 16);
    
    const counter = setInterval(() => {
        current += increment;
        if (current >= target) {
            element.textContent = target.toLocaleString();
            clearInterval(counter);
        } else {
            element.textContent = Math.floor(current).toLocaleString();
        }
    }, 16);
}

// ===== SCROLL REVEAL PARA SECCIONES =====
function setupScrollReveal() {
    const revealElements = document.querySelectorAll('[data-reveal]');
    
    if (revealElements.length === 0) return;
    
    const observerOptions = {
        threshold: 0.2,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                entry.target.style.animation = `slideInUp 0.6s ease ${index * 0.1}s both`;
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    revealElements.forEach(el => observer.observe(el));
}

// ===== BÚSQUEDA DESDE INICIO =====
function setupSearchFromHome() {
    const searchInput = document.querySelector('.search-bar input');
    if (!searchInput) return;
    
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const query = searchInput.value.trim();
            if (query) {
                window.location.href = `/productos?search=${encodeURIComponent(query)}`;
            }
        }
    });
}

// ===== INICIALIZAR PÁGINA DE INICIO =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Inicializando página de inicio...');
    
    renderFeaturedProducts();
    setupNewsletterForm();
    setupCategoryAnimations();
    setupScrollReveal();
    setupSearchFromHome();
    
    console.log('✅ Página de inicio lista con todas las animaciones');
});
