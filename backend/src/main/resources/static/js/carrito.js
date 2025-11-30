// ===== VARIABLES GLOBALES DE CHECKOUT =====
let selectedPaymentMethod = null;
let selectedDocumentType = null;
let checkoutData = {
    paymentMethod: null,
    documentType: null,
    email: null,
    customerInfo: {},
    total: 0,
    subtotal: 0,
    taxes: 0
};

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
        
        // Deshabilitar botón de checkout si carrito vacío
        const checkoutBtn = document.getElementById('checkoutBtn');
        if (checkoutBtn) {
            checkoutBtn.disabled = true;
            checkoutBtn.style.opacity = '0.5';
            checkoutBtn.style.cursor = 'not-allowed';
        }
        return;
    }

    // Habilitar botón de checkout
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.disabled = false;
        checkoutBtn.style.opacity = '1';
        checkoutBtn.style.cursor = 'pointer';
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
                        <p>Precio: S/. ${item.price.toFixed(2)}</p>
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
    const taxRate = 0.18;
    const taxes = subtotal * taxRate;
    const total = subtotal + taxes;

    const subtotalEl = document.getElementById('subtotal');
    const taxesEl = document.getElementById('taxes');
    const totalEl = document.getElementById('total');
    
    if (subtotalEl) subtotalEl.textContent = `S/. ${subtotal.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
    if (taxesEl) taxesEl.textContent = `S/. ${taxes.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
    if (totalEl) {
        totalEl.textContent = `S/. ${total.toLocaleString('es-PE', {minimumFractionDigits: 2})}`;
        totalEl.style.animation = 'pulse 0.6s ease';
    }
    
    checkoutData.total = total;
    checkoutData.subtotal = subtotal;
    checkoutData.taxes = taxes;
}

// ===== ABRIR MODAL DE CHECKOUT =====
function openCheckoutModal() {
    if (cart.items.length === 0) {
        cart.showNotification('Tu carrito está vacío', 'warning');
        return;
    }
    
    const modal = document.getElementById('checkoutModal');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        showStep(1);
        resetCheckoutState();
        setTimeout(() => modal.classList.add('active'), 10);
    }
}

// ===== CERRAR MODAL =====
function closeCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    if (modal) {
        modal.classList.remove('active');
        setTimeout(() => {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }, 300);
    }
}

// ===== RESETEAR ESTADO DEL CHECKOUT =====
function resetCheckoutState() {
    selectedPaymentMethod = null;
    selectedDocumentType = null;
    
    document.querySelectorAll('.payment-method').forEach(el => el.classList.remove('selected'));
    document.querySelectorAll('.document-type').forEach(el => el.classList.remove('selected'));
    
    const step2Btn = document.getElementById('continueToStep2');
    const step3Btn = document.getElementById('continueToStep3');
    if (step2Btn) step2Btn.disabled = true;
    if (step3Btn) step3Btn.disabled = true;
    
    const facturaFields = document.getElementById('facturaFields');
    const boletaFields = document.getElementById('boletaFields');
    if (facturaFields) facturaFields.style.display = 'none';
    if (boletaFields) boletaFields.style.display = 'none';
    
    ['ruc', 'razonSocial', 'direccionFiscal', 'dni', 'nombreCliente', 'emailComprobante'].forEach(id => {
        const input = document.getElementById(id);
        if (input) input.value = '';
    });
}

// ===== MOSTRAR PASO ESPECÍFICO =====
function showStep(stepNumber) {
    document.querySelectorAll('.checkout-step').forEach(step => step.classList.remove('active'));
    const step = document.getElementById(`step${stepNumber}`);
    if (step) step.classList.add('active');
}

// ===== SELECCIONAR MÉTODO DE PAGO =====
function selectPaymentMethod(method) {
    selectedPaymentMethod = method;
    checkoutData.paymentMethod = method;
    
    document.querySelectorAll('.payment-method').forEach(el => el.classList.remove('selected'));
    const selected = document.querySelector(`[data-method="${method}"]`);
    if (selected) {
        selected.classList.add('selected');
        selected.style.animation = 'pulse 0.3s ease';
    }
    
    const continueBtn = document.getElementById('continueToStep2');
    if (continueBtn) {
        continueBtn.disabled = false;
        continueBtn.style.animation = 'pulse 0.3s ease';
    }
}

// ===== SELECCIONAR TIPO DE DOCUMENTO =====
function selectDocumentType(type) {
    selectedDocumentType = type;
    checkoutData.documentType = type;
    
    document.querySelectorAll('.document-type').forEach(el => el.classList.remove('selected'));
    const selected = document.querySelector(`[data-type="${type}"]`);
    if (selected) selected.classList.add('selected');
    
    const facturaFields = document.getElementById('facturaFields');
    const boletaFields = document.getElementById('boletaFields');
    
    if (type === 'factura') {
        if (facturaFields) { facturaFields.style.display = 'block'; facturaFields.style.animation = 'fadeIn 0.3s ease'; }
        if (boletaFields) boletaFields.style.display = 'none';
    } else {
        if (boletaFields) { boletaFields.style.display = 'block'; boletaFields.style.animation = 'fadeIn 0.3s ease'; }
        if (facturaFields) facturaFields.style.display = 'none';
    }
    
    validateStep2();
}

// ===== VALIDAR PASO 2 =====
function validateStep2() {
    const email = document.getElementById('emailComprobante')?.value?.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    let isValid = selectedDocumentType && email && emailRegex.test(email);
    
    if (selectedDocumentType === 'factura') {
        const ruc = document.getElementById('ruc')?.value?.trim();
        const razonSocial = document.getElementById('razonSocial')?.value?.trim();
        const direccion = document.getElementById('direccionFiscal')?.value?.trim();
        isValid = isValid && ruc?.length === 11 && razonSocial && direccion;
    } else if (selectedDocumentType === 'boleta') {
        const dni = document.getElementById('dni')?.value?.trim();
        const nombre = document.getElementById('nombreCliente')?.value?.trim();
        isValid = isValid && dni?.length === 8 && nombre;
    }
    
    const continueBtn = document.getElementById('continueToStep3');
    if (continueBtn) continueBtn.disabled = !isValid;
    return isValid;
}

// ===== NAVEGACIÓN ENTRE PASOS =====
function goToStep2() {
    if (!selectedPaymentMethod) {
        cart.showNotification('Selecciona un método de pago', 'warning');
        return;
    }
    showStep(2);
}

function goToStep1() { showStep(1); }

// ===== PROCESAR PAGO =====
function processPayment() {
    if (!validateStep2()) {
        cart.showNotification('Completa todos los campos requeridos', 'warning');
        return;
    }
    
    checkoutData.email = document.getElementById('emailComprobante')?.value?.trim();
    
    if (selectedDocumentType === 'factura') {
        checkoutData.customerInfo = {
            ruc: document.getElementById('ruc')?.value?.trim(),
            razonSocial: document.getElementById('razonSocial')?.value?.trim(),
            direccion: document.getElementById('direccionFiscal')?.value?.trim()
        };
    } else {
        checkoutData.customerInfo = {
            dni: document.getElementById('dni')?.value?.trim(),
            nombre: document.getElementById('nombreCliente')?.value?.trim()
        };
    }
    
    showStep(3);
    
    const methodNames = { 'paypal': 'PayPal', 'yape': 'Yape', 'plin': 'Plin' };
    const methodNameEl = document.getElementById('paymentMethodName');
    if (methodNameEl) methodNameEl.textContent = methodNames[selectedPaymentMethod] || selectedPaymentMethod;
    
    simulatePaymentProcess();
}

// ===== SIMULAR PROCESO DE PAGO =====
function simulatePaymentProcess() {
    const methodName = getPaymentMethodName();
    const messages = [
        `Conectando con ${methodName}...`,
        'Verificando datos de pago...',
        'Procesando transacción...',
        'Generando comprobante...',
        'Enviando confirmación por correo...'
    ];
    
    let messageIndex = 0;
    const messageEl = document.getElementById('processingMessage');
    
    const interval = setInterval(() => {
        messageIndex++;
        if (messageIndex < messages.length && messageEl) {
            messageEl.textContent = messages[messageIndex];
        }
    }, 600);
    
    setTimeout(() => {
        clearInterval(interval);
        showPaymentSuccess();
    }, 3000);
}

function getPaymentMethodName() {
    return { 'paypal': 'PayPal', 'yape': 'Yape', 'plin': 'Plin' }[selectedPaymentMethod] || selectedPaymentMethod;
}

// ===== MOSTRAR ÉXITO DEL PAGO =====
function showPaymentSuccess() {
    showStep(4);
    
    const orderNumber = 'ORD-' + new Date().getFullYear() + '-' + Math.random().toString(36).substr(2, 6).toUpperCase();
    
    document.getElementById('orderNumber').textContent = orderNumber;
    document.getElementById('paymentMethodUsed').textContent = getPaymentMethodName();
    document.getElementById('documentTypeUsed').textContent = selectedDocumentType === 'factura' ? 'Factura Electrónica' : 'Boleta de Venta';
    document.getElementById('totalPaid').textContent = `S/. ${checkoutData.total?.toLocaleString('es-PE', {minimumFractionDigits: 2}) || '0.00'}`;
    document.getElementById('documentSent').textContent = selectedDocumentType === 'factura' ? 'factura electrónica' : 'boleta de venta';
    document.getElementById('emailSent').textContent = checkoutData.email;
    
    // Limpiar carrito
    cart.items = [];
    cart.save();
    cart.updateBadge();
    
    createConfetti();
    cart.showNotification('🎉 ¡Pago exitoso! Tu comprobante ha sido enviado al correo.', 'success');
    
    console.log('📦 Orden completada:', { orderNumber, ...checkoutData });
}

// ===== CREAR EFECTO CONFETTI =====
function createConfetti() {
    const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#43e97b', '#ffd700'];
    for (let i = 0; i < 100; i++) {
        const confetti = document.createElement('div');
        confetti.style.cssText = `
            position: fixed; width: 10px; height: 10px;
            background: ${colors[Math.floor(Math.random() * colors.length)]};
            left: ${Math.random() * 100}vw; top: -10px;
            opacity: ${Math.random()}; transform: rotate(${Math.random() * 360}deg);
            animation: confettiFall ${2 + Math.random() * 3}s linear forwards;
            z-index: 10001; pointer-events: none; border-radius: 2px;
        `;
        document.body.appendChild(confetti);
        setTimeout(() => confetti.remove(), 5000);
    }
}

// ===== EVENT LISTENERS =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Inicializando carrito con checkout completo...');
    
    renderCartItems();
    updateActiveNav();
    
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) checkoutBtn.addEventListener('click', openCheckoutModal);
    
    ['emailComprobante', 'ruc', 'razonSocial', 'direccionFiscal', 'dni', 'nombreCliente'].forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            field.addEventListener('input', validateStep2);
            field.addEventListener('blur', validateStep2);
        }
    });
    
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') closeCheckoutModal();
    });
    
    console.log('✅ Carrito listo con checkout PayPal, Yape y Plin');
});

// ===== ESTILOS DE CONFETTI =====
const confettiStyles = document.createElement('style');
confettiStyles.textContent = `@keyframes confettiFall { 0% { transform: translateY(0) rotate(0deg); opacity: 1; } 100% { transform: translateY(100vh) rotate(720deg); opacity: 0; } }`;
document.head.appendChild(confettiStyles);
