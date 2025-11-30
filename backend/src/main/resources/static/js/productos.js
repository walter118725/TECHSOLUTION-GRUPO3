// ===== PRODUCTOS CON EMOJIS Y DATOS REALISTAS =====
const allProducts = [
    { id: 1, name: 'MacBook Pro 16" M3', price: 3999, oldPrice: 4399, category: 'computadoras', discount: 9, emoji: '💻', rating: 234 },
    { id: 2, name: 'iPhone 15 Pro Max', price: 1299, oldPrice: 1499, category: 'smartphones', discount: 13, emoji: '📱', rating: 512 },
    { id: 3, name: 'Samsung Galaxy S24', price: 899, oldPrice: 1099, category: 'smartphones', discount: 18, emoji: '📱', rating: 389 },
    { id: 4, name: 'Sony WH-1000XM5', price: 399, oldPrice: 499, category: 'audio', discount: 20, emoji: '🎧', rating: 189 },
    { id: 5, name: 'PS5 Console', price: 599, oldPrice: 699, category: 'gaming', discount: 14, emoji: '🎮', rating: 426 },
    { id: 6, name: 'Xbox Series X', price: 499, oldPrice: 599, category: 'gaming', discount: 17, emoji: '🎮', rating: 312 },
    { id: 7, name: 'Apple Watch Series 9', price: 399, oldPrice: 499, category: 'wearables', discount: 20, emoji: '⌚', rating: 312 },
    { id: 8, name: 'Oculus Quest 3', price: 499, oldPrice: 599, category: 'wearables', discount: 17, emoji: '👓', rating: 245 },
    { id: 9, name: 'iPad Pro 12.9"', price: 1199, oldPrice: 1399, category: 'computadoras', discount: 14, emoji: '📱', rating: 523 },
    { id: 10, name: 'Samsung Monitor 4K', price: 699, oldPrice: 899, category: 'accesorios', discount: 22, emoji: '🖥️', rating: 178 },
    { id: 11, name: 'AirPods Pro Max', price: 549, oldPrice: 649, category: 'audio', discount: 15, emoji: '🎧', rating: 456 },
    { id: 12, name: 'GoPro Hero 12', price: 449, oldPrice: 549, category: 'accesorios', discount: 18, emoji: '📹', rating: 234 },
];

let filteredProducts = [...allProducts];

// ===== VERIFICAR AUTENTICACIÓN =====
function isUserAuthenticated() {
    return window.isAuthenticated === true;
}

// ===== AGREGAR AL CARRITO CON VERIFICACIÓN =====
function addToCartWithAuth(product) {
    if (!isUserAuthenticated()) {
        showLoginModal();
        return;
    }
    
    cart.add(product);
    showNotification(`✅ ${product.name} agregado al carrito`);
}

// ===== MOSTRAR MODAL DE LOGIN =====
function showLoginModal() {
    // Crear modal si no existe
    let modal = document.getElementById('loginModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'loginModal';
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content" style="animation: slideInUp 0.3s ease;">
                <div class="modal-header">
                    <h3>🔐 Iniciar Sesión Requerido</h3>
                    <button class="modal-close" onclick="closeLoginModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <p style="margin-bottom: 20px; color: #666;">Para agregar productos al carrito, necesitas iniciar sesión con tu cuenta.</p>
                    <div class="modal-accounts">
                        <p style="font-size: 13px; color: #999; margin-bottom: 12px;">Cuentas de prueba disponibles:</p>
                        <ul style="list-style: none; padding: 0; margin: 0 0 20px;">
                            <li style="padding: 8px; background: #f5f5f5; border-radius: 6px; margin-bottom: 6px;">
                                <span style="background:#16a34a;color:#fff;padding:2px 6px;border-radius:10px;font-size:10px;">CLIENTE</span>
                                <strong>cliente</strong> / cliente123
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="/login" class="btn btn-primary" style="flex:1; text-align:center;">Iniciar Sesión</a>
                    <button class="btn btn-secondary" onclick="closeLoginModal()" style="flex:1;">Cancelar</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeLoginModal() {
    const modal = document.getElementById('loginModal');
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

// ===== MOSTRAR NOTIFICACIÓN =====
function showNotification(message) {
    let notification = document.getElementById('notification');
    if (!notification) {
        notification = document.createElement('div');
        notification.id = 'notification';
        notification.className = 'notification';
        document.body.appendChild(notification);
    }
    notification.textContent = message;
    notification.classList.add('show');
    setTimeout(() => notification.classList.remove('show'), 3000);
}

// ===== RENDERIZAR PRODUCTOS CON ANIMACIONES =====
function renderProducts() {
    const container = document.getElementById('productsContainer');
    if (!container) return;

    if (filteredProducts.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="grid-column: 1/-1; text-align: center; padding: 60px 20px; animation: fadeIn 0.5s ease;">
                <div style="font-size: 60px; margin-bottom: 16px;">🔍</div>
                <p style="font-size: 18px; color: #666; margin-bottom: 16px;">No se encontraron productos</p>
                <button class="btn btn-primary" onclick="location.href='/productos'" style="transition: all 0.3s ease;">
                    Ver todos los productos
                </button>
            </div>
        `;
        return;
    }

    const isAuth = isUserAuthenticated();
    
    container.innerHTML = filteredProducts.map((product, index) => `
        <div class="product-card" style="animation-delay: ${index * 0.05}s">
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
                <div class="product-rating">⭐⭐⭐⭐⭐ (${product.rating})</div>
                <div class="product-actions">
                    ${isAuth ? `
                        <button class="btn btn-primary btn-sm" onclick="
                            addToCartWithAuth({
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
                    ` : `
                        <button class="btn btn-login-required btn-sm" onclick="showLoginModal()" style="flex: 1; transition: all 0.2s ease;">
                            🔐 Inicia sesión para comprar
                        </button>
                    `}
                </div>
            </div>
        </div>
    `).join('');
}

// ===== FILTROS CON BÚSQUEDA Y CATEGORÍAS =====
function filterProducts() {
    const searchInput = document.getElementById('searchInput');
    // Usar IDs reales del template: categoryFilter y priceFilter
    const categorySelect = document.getElementById('categoryFilter');
    const priceSelect = document.getElementById('priceFilter');

    const search = (searchInput?.value || '').toLowerCase();
    const category = categorySelect?.value || '';
    const priceRange = priceSelect?.value || '';

    filteredProducts = allProducts.filter(product => {
        const matchSearch = product.name.toLowerCase().includes(search);
        const matchCategory = !category || product.category === category;
        const matchPrice = !priceRange || checkPriceRange(product.price, priceRange);
        return matchSearch && matchCategory && matchPrice;
    });

    const container = document.getElementById('productsContainer');
    if (container) {
        container.style.opacity = '0.5';
        container.style.transform = 'translateY(10px)';
        setTimeout(() => {
            renderProducts();
            container.style.animation = 'fadeIn 0.3s ease forwards';
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 100);
    }
}

// ===== VALIDAR RANGO DE PRECIO =====
function checkPriceRange(price, range) {
    // Formatos esperados en template: "0-500", "500-1000", "1000+"
    if (!range) return true;
    if (range.includes('-')) {
        const [minStr, maxStr] = range.split('-');
        const min = parseInt(minStr, 10);
        const max = parseInt(maxStr, 10);
        return price >= min && price <= max;
    }
    if (range.endsWith('+')) {
        const min = parseInt(range.replace('+',''), 10);
        return price >= min;
    }
    return true;
}

// ===== EVENT LISTENERS PARA FILTROS =====
function setupFilterListeners() {
    const searchInput = document.getElementById('searchInput');
    const categorySelect = document.getElementById('categoryFilter');
    const priceSelect = document.getElementById('priceFilter');

    if (searchInput) {
        searchInput.addEventListener('input', filterProducts);
        searchInput.addEventListener('focus', function() {
            this.style.boxShadow = '0 2px 8px rgba(236, 28, 36, 0.2)';
        });
        searchInput.addEventListener('blur', function() {
            this.style.boxShadow = 'none';
        });
    }

    if (categorySelect) {
        categorySelect.addEventListener('change', filterProducts);
    }

    if (priceSelect) {
        priceSelect.addEventListener('change', filterProducts);
    }
}

// ===== BÚSQUEDA DESDE URL =====
function handleUrlSearch() {
    const params = new URLSearchParams(window.location.search);
    const searchQuery = params.get('search');
    
    if (searchQuery) {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.value = searchQuery;
            filterProducts();
        }
    }
}

// ===== ANIMACIÓN DE CARGA =====
function setupLoadingAnimation() {
    const container = document.getElementById('productsContainer');
    if (container) {
        container.style.opacity = '0';
        container.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            container.style.transition = 'all 0.5s ease';
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 100);
    }
}

// ===== INICIALIZAR PÁGINA DE PRODUCTOS =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Inicializando página de productos...');
    
    renderProducts();
    setupFilterListeners();
    handleUrlSearch();
    setupLoadingAnimation();
    updateActiveNav();
    
    console.log('✅ Página de productos lista con filtros y animaciones');
});
