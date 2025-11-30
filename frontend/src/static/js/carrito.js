// ===== RENDERIZAR CARRITO CON ANIMACIONES =====
function renderCartItems() {
    const container = document.getElementById('cartItemsContainer');
    if (!container) return;

    if (cart.items.length === 0) {
        container.innerHTML = `
            <div class="empty-cart" style="animation: fadeIn 0.5s ease;">
                <div class="empty-icon" style="animation: float 3s ease-in-out infinite;">🛒</div>
                <h3>Tu carrito está vacío</h3>
                <p>¡Explora nuestros productos y agrega algo a tu carrito!</p>
                <a href="/productos" class="btn btn-primary" style="transition: all 0.3s ease;">
                    🛍️ Ir a Productos
                </a>
            </div>
        `;
        updateCartSummary();
        return;
    }

    container.innerHTML = `
        <div class="cart-items-list">
            ${cart.items.map((item, index) => `
                <div class="cart-item" data-product-id="${item.id}" style="animation-delay: ${index * 0.05}s">
                    <div class="item-image" style="animation: float 3s ease-in-out infinite;">
                        ${item.emoji || '📦'}
                    </div>
                    <div class="item-details">
                        <h4>${item.name}</h4>
                        <p>Precio: S/. ${item.price}</p>
                    </div>
                    <div class="item-quantity">
                        <button onclick="updateQuantityAndRender(${item.id}, ${item.quantity - 1})" 
                                class="qty-btn" 
                                title="Disminuir cantidad"
                                style="transition: all 0.2s ease;">−</button>
                        <input type="number" value="${item.quantity}" min="1" readonly 
                               style="transition: all 0.2s ease;">
                        <button onclick="updateQuantityAndRender(${item.id}, ${item.quantity + 1})" 
                                class="qty-btn" 
                                title="Aumentar cantidad"
                                style="transition: all 0.2s ease;">+</button>
                    </div>
                    <div class="item-total" style="animation: pulse 2s ease-in-out infinite;">
                        S/. ${(item.price * item.quantity).toLocaleString('es-PE', {minimumFractionDigits: 2})}
                    </div>
                    <button onclick="removeItemAndRender(${item.id})" 
                            class="item-remove" 
                            title="Eliminar del carrito"
                            style="transition: all 0.2s ease; cursor: pointer;">🗑️</button>
                </div>
            `).join('')}
        </div>
    `;

    updateCartSummary();
}

// ===== ACTUALIZAR CANTIDAD CON VALIDACIÓN =====
function updateQuantityAndRender(productId, newQuantity) {
    const item = cart.items.find(p => p.id === productId);
    if (item) {
        if (newQuantity <= 0) {
            removeItemAndRender(productId);
            return;
        }
        
        item.quantity = newQuantity;
        cart.save();
        
        // Animación de cambio
        const container = document.getElementById('cartItemsContainer');
        if (container) {
            container.style.opacity = '0.7';
            container.style.transform = 'scale(0.98)';
            
            setTimeout(() => {
                renderCartItems();
                container.style.animation = 'fadeIn 0.3s ease forwards';
                container.style.opacity = '1';
                container.style.transform = 'scale(1)';
            }, 100);
        }
    }
}

// ===== REMOVER ITEM CON ANIMACIÓN =====
function removeItemAndRender(productId) {
    const item = cart.items.find(p => p.id === productId);
    if (item) {
        const container = document.getElementById('cartItemsContainer');
        const itemElement = container?.querySelector(`[data-product-id="${productId}"]`);
        
        if (itemElement) {
            itemElement.style.animation = 'slideOutRight 0.3s ease forwards';
            
            setTimeout(() => {
                cart.remove(productId);
                renderCartItems();
            }, 300);
        } else {
            cart.remove(productId);
            renderCartItems();
        }
    }
}

// ===== ACTUALIZAR RESUMEN DEL CARRITO =====
function updateCartSummary() {
    const subtotal = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const taxRate = 0.18; // 18% IVA en Perú
    const taxes = subtotal * taxRate;
    const total = subtotal + taxes;

    const subtotalEl = document.getElementById('subtotal');
    const taxesEl = document.getElementById('taxes');
    const totalEl = document.getElementById('total');
    const itemCountEl = document.getElementById('itemCount');
    
    if (subtotalEl) subtotalEl.textContent = `S/. ${subtotal.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
    if (taxesEl) taxesEl.textContent = `S/. ${taxes.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
    if (totalEl) {
        totalEl.textContent = `S/. ${total.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
        totalEl.style.animation = 'pulse 0.6s ease';
    }
    if (itemCountEl) {
        const itemCount = cart.items.reduce((sum, item) => sum + item.quantity, 0);
        itemCountEl.textContent = `${itemCount} ${itemCount === 1 ? 'artículo' : 'artículos'}`;
    }
}

// ===== CHECKOUT CON VALIDACIÓN Y ANIMACIÓN =====
function handleCheckout() {
    if (cart.items.length === 0) {
        cart.showNotification('Tu carrito está vacío', 'warning');
        return;
    }
    
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.disabled = true;
        checkoutBtn.style.opacity = '0.6';
        checkoutBtn.textContent = '⏳ Procesando...';
    }
    
    const total = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
    // Simulación de procesamiento
    setTimeout(() => {
        cart.showNotification('✅ Pago procesado exitosamente', 'success');
        
        setTimeout(() => {
            cart.items = [];
            cart.save();
            cart.updateBadge();
            renderCartItems();
            
            cart.showNotification('🎉 ¡Pedido completado! Gracias por tu compra.', 'success');
            
            if (checkoutBtn) {
                checkoutBtn.disabled = false;
                checkoutBtn.style.opacity = '1';
                checkoutBtn.textContent = '💳 Ir a Pagar';
            }
        }, 500);
    }, 1200);
}

// ===== APLICAR CUPÓN =====
function applyCoupon() {
    const couponInput = document.getElementById('couponInput');
    if (!couponInput) return;
    
    const couponCode = couponInput.value.trim().toUpperCase();
    
    // Cupones de prueba
    const validCoupons = {
        'DESCUENTO10': 0.10,
        'DESCUENTO20': 0.20,
        'FALABELLA': 0.15
    };
    
    if (!couponCode) {
        cart.showNotification('Por favor, ingresa un código de cupón', 'warning');
        return;
    }
    
    if (validCoupons[couponCode]) {
        const discount = validCoupons[couponCode];
        cart.showNotification(`✅ Cupón aplicado: ${(discount * 100).toFixed(0)}% de descuento`, 'success');
        
        // Aquí iría la lógica para aplicar el descuento
        couponInput.value = '';
        couponInput.style.animation = 'pulse 0.5s ease';
    } else {
        cart.showNotification('❌ Cupón inválido', 'error');
        couponInput.style.animation = 'pulse 0.5s ease';
    }
}

// ===== EVENT LISTENERS =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Inicializando página de carrito...');
    
    renderCartItems();
    updateActiveNav();
    
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', handleCheckout);
    }
    
    const couponBtn = document.getElementById('applyCouponBtn');
    if (couponBtn) {
        couponBtn.addEventListener('click', applyCoupon);
    }
    
    const couponInput = document.getElementById('couponInput');
    if (couponInput) {
        couponInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') applyCoupon();
        });
    }
    
    console.log('✅ Página de carrito lista con todas las funcionalidades');
});

// ===== OBSERVER PARA ANIMACIONES DE RESUMEN =====
document.addEventListener('DOMContentLoaded', () => {
    const summaryCard = document.querySelector('.summary-card');
    if (summaryCard) {
        summaryCard.style.animation = 'slideInRight 0.5s ease';
    }
});
