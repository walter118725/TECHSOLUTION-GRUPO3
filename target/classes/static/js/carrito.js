// ============================================
// Carrito de Compras - Interacciones
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    loadCartItems();
});

function loadCartItems() {
    const cart = getCart();
    const emptyCart = document.getElementById('emptyCart');
    const cartItems = document.getElementById('cartItems');
    const itemsList = document.getElementById('itemsList');
    
    if (cart.length === 0) {
        emptyCart.style.display = 'block';
        cartItems.style.display = 'none';
        return;
    }
    
    emptyCart.style.display = 'none';
    cartItems.style.display = 'block';
    
    // Limpiar lista
    itemsList.innerHTML = '';
    
    let subtotal = 0;
    
    // Renderizar items
    cart.forEach((item, index) => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;
        
        const itemCard = document.createElement('div');
        itemCard.className = 'feature-card fade-in';
        itemCard.style.animationDelay = `${index * 0.1}s`;
        itemCard.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; gap: 20px;">
                <div style="font-size: 48px;">${item.emoji}</div>
                <div style="flex: 1;">
                    <h3 style="color: #4f46e5; margin-bottom: 10px;">${item.name}</h3>
                    <div style="color: #6b7280; font-size: 18px; margin-bottom: 8px;">
                        Precio unitario: ${formatPrice(item.price)}
                    </div>
                    <div style="display: flex; align-items: center; gap: 15px;">
                        <button class="btn btn-primary" onclick="updateQuantity(${item.id}, -1)" style="padding: 8px 16px;">
                            ➖
                        </button>
                        <span style="font-size: 20px; font-weight: 600; min-width: 40px; text-align: center;">
                            ${item.quantity}
                        </span>
                        <button class="btn btn-primary" onclick="updateQuantity(${item.id}, 1)" style="padding: 8px 16px;">
                            ➕
                        </button>
                    </div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 24px; font-weight: 700; color: #4f46e5; margin-bottom: 10px;">
                        ${formatPrice(itemTotal)}
                    </div>
                    <button class="btn" onclick="removeFromCart(${item.id})" 
                            style="background: #ef4444; color: white; padding: 8px 16px;">
                        🗑️ Eliminar
                    </button>
                </div>
            </div>
        `;
        
        itemsList.appendChild(itemCard);
    });
    
    // Actualizar totales
    document.getElementById('subtotal').textContent = formatPrice(subtotal);
    document.getElementById('total').textContent = formatPrice(subtotal);
}

function updateQuantity(productId, change) {
    let cart = getCart();
    const item = cart.find(i => i.id === productId);
    
    if (!item) return;
    
    item.quantity += change;
    
    if (item.quantity <= 0) {
        removeFromCart(productId);
        return;
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
    
    if (change > 0) {
        showNotification('✅ Cantidad actualizada', 'success');
    } else {
        showNotification('➖ Cantidad reducida', 'info');
    }
    
    loadCartItems();
    updateCartCount();
}

function removeFromCart(productId) {
    let cart = getCart();
    const item = cart.find(i => i.id === productId);
    
    if (!item) return;
    
    cart = cart.filter(i => i.id !== productId);
    localStorage.setItem('cart', JSON.stringify(cart));
    
    showNotification(`🗑️ ${item.name} eliminado del carrito`, 'warning');
    
    loadCartItems();
    updateCartCount();
}

function procesarCompra() {
    const cart = getCart();
    
    if (cart.length === 0) {
        showNotification('❌ Tu carrito está vacío. Agrega productos primero.', 'warning');
        return;
    }
    
    const paymentMethod = document.getElementById('paymentMethod').value;
    
    if (!paymentMethod) {
        showNotification('⚠️ Por favor, selecciona un método de pago', 'warning');
        return;
    }
    
    const button = event.target;
    const originalText = button.innerHTML;
    showLoading(button);
    
    // Calcular total
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
    // Simular procesamiento
    setTimeout(() => {
        // Preparar datos de la venta
        const ventaData = {
            items: cart.map(item => ({
                productoId: item.id,
                cantidad: item.quantity,
                precioUnitario: item.price
            })),
            metodoPago: paymentMethod,
            total: total
        };
        
        // Llamar al API de pagos
        fetch('/api/pagos/procesar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                pasarelaId: paymentMethod,
                monto: total,
                referencia: `COMPRA-${Date.now()}`
            })
        })
        .then(response => response.json())
        .then(data => {
            hideLoading(button, originalText);
            
            if (data.exitoso) {
                showNotification('🎉 ¡Compra realizada con éxito! Gracias por tu preferencia.', 'success');
                
                // Limpiar carrito
                clearCart();
                
                // Redirigir después de 2 segundos
                setTimeout(() => {
                    window.location.href = '/';
                }, 2000);
            } else {
                showNotification(`❌ Error: ${data.mensaje}`, 'warning');
            }
        })
        .catch(error => {
            hideLoading(button, originalText);
            console.error('Error:', error);
            showNotification('❌ Hubo un problema al procesar tu compra. Por favor, intenta nuevamente.', 'warning');
        });
    }, 1000);
}

// Exportar funciones globales
window.updateQuantity = updateQuantity;
window.removeFromCart = removeFromCart;
window.procesarCompra = procesarCompra;
