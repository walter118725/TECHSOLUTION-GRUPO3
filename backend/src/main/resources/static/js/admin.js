/**
 * Admin Dashboard - JavaScript
 * TechSolutions 2025
 * Gestión de Administración e Inventario
 */

// Variables globales
let productos = [];
let productoEditandoId = null;
let productoEliminarId = null;
let promociones = [
    { id: 1, codigo: 'PROMO-001', nombre: 'Black Friday 2025', descuento: 25, fechaInicio: '2025-11-28', fechaFin: '2025-11-30', estado: 'active' },
    { id: 2, codigo: 'PROMO-002', nombre: 'Cyber Monday', descuento: 30, fechaInicio: '2025-12-01', fechaFin: '2025-12-02', estado: 'scheduled' }
];

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Admin Dashboard inicializado');
    initializeDate();
    loadPromociones();
    loadProductos();
    
    const searchInput = document.getElementById('searchProducto');
    if (searchInput) {
        searchInput.addEventListener('input', filterProductos);
    }
    
    // Agregar event listeners explícitos para los botones
    const btnAgregar = document.querySelector('.btn-primary.btn-lg');
    if (btnAgregar) {
        console.log('✅ Botón Agregar Producto encontrado');
        btnAgregar.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('🖱️ Click en botón Agregar');
            abrirModalNuevoProducto();
        });
    } else {
        console.log('❌ Botón Agregar Producto NO encontrado');
    }
});

// ============================================
// FUNCIONES DE TAB
// ============================================

function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById('tab-' + tabName).classList.add('active');
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
}

// ============================================
// FECHA ACTUAL
// ============================================

function initializeDate() {
    const dateElement = document.getElementById('currentDate');
    if (dateElement) {
        const now = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        dateElement.textContent = '📅 ' + now.toLocaleDateString('es-PE', options);
    }
}

// ============================================
// PROMOCIONES
// ============================================

function loadPromociones() {
    const tbody = document.getElementById('promocionesTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    promociones.forEach(promo => {
        const statusClass = promo.estado === 'active' ? 'active' : 'scheduled';
        const statusText = promo.estado === 'active' ? 'Activa' : 'Programada';
        tbody.innerHTML += `
            <tr>
                <td><code>${promo.codigo}</code></td>
                <td><strong>${promo.nombre}</strong></td>
                <td><span class="discount-badge">${promo.descuento}%</span></td>
                <td>${promo.fechaInicio}</td>
                <td>${promo.fechaFin}</td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>
                    <button class="btn-icon edit" onclick="editarPromocion(${promo.id})">✏️</button>
                    <button class="btn-icon delete" onclick="eliminarPromocion(${promo.id})">🗑️</button>
                </td>
            </tr>`;
    });
    updatePromoStats();
}

function updatePromoStats() {
    const el = (id) => document.getElementById(id);
    if (el('promoActivas')) el('promoActivas').textContent = promociones.filter(p => p.estado === 'active').length;
    if (el('promoProgramadas')) el('promoProgramadas').textContent = promociones.filter(p => p.estado === 'scheduled').length;
    if (el('promoFinalizadas')) el('promoFinalizadas').textContent = '0';
    if (el('descuentoPromedio')) {
        const avg = promociones.length > 0 ? promociones.reduce((a, b) => a + b.descuento, 0) / promociones.length : 0;
        el('descuentoPromedio').textContent = Math.round(avg) + '%';
    }
}

function abrirModalNuevaPromocion() {
    document.getElementById('modalNuevaPromocion').style.display = 'flex';
}

function cerrarModalPromocion() {
    document.getElementById('modalNuevaPromocion').style.display = 'none';
}

function guardarPromocion() {
    const nombre = document.getElementById('nombrePromocion').value;
    const descuento = document.getElementById('descuentoPromocion').value;
    if (!nombre || !descuento) { alert('Complete todos los campos'); return; }
    
    promociones.push({
        id: Date.now(),
        codigo: 'PROMO-' + String(promociones.length + 1).padStart(3, '0'),
        nombre, descuento: parseInt(descuento),
        fechaInicio: document.getElementById('fechaInicioPromo').value || new Date().toISOString().split('T')[0],
        fechaFin: document.getElementById('fechaFinPromo').value || new Date().toISOString().split('T')[0],
        estado: 'scheduled'
    });
    cerrarModalPromocion();
    loadPromociones();
    mostrarExito('¡Promoción Creada!', 'La promoción se ha guardado.');
}

function editarPromocion(id) { alert('Editar promoción ' + id); }
function eliminarPromocion(id) {
    if (confirm('¿Eliminar esta promoción?')) {
        promociones = promociones.filter(p => p.id !== id);
        loadPromociones();
    }
}

// ============================================
// PRODUCTOS - CARGAR
// ============================================

async function loadProductos() {
    console.log('📦 Cargando productos...');
    try {
        const response = await fetch('/api/inventario/productos');
        if (response.ok) {
            productos = await response.json();
            console.log('✅ Productos cargados:', productos.length);
        } else {
            console.error('❌ Error cargando productos:', response.status);
            productos = [];
        }
    } catch (error) {
        console.error('❌ Error de red:', error);
        productos = [];
    }
    renderProductos(productos);
}

function renderProductos(lista) {
    const tbody = document.getElementById('productosTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:40px;"><h3>📦 No hay productos</h3><p>Agrega un nuevo producto</p></td></tr>';
        updateProductStats([]);
        return;
    }
    
    lista.forEach(prod => {
        const stock = prod.stock || 0;
        const stockMin = prod.stockMinimo || 5;
        const stockClass = stock === 0 ? 'out-of-stock' : stock <= stockMin ? 'low-stock' : 'in-stock';
        const stockText = stock === 0 ? 'Sin stock' : stock <= stockMin ? `⚠️ ${stock}` : `${stock} uds`;
        const statusClass = prod.activo !== false ? 'active' : 'inactive';
        const statusText = prod.activo !== false ? 'Activo' : 'Inactivo';
        
        tbody.innerHTML += `
            <tr>
                <td><div style="width:50px;height:50px;background:#f0f0f0;border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:24px;">📦</div></td>
                <td><code>${prod.codigo || 'N/A'}</code></td>
                <td><strong>${prod.nombre}</strong></td>
                <td>${prod.categoria || '-'}</td>
                <td><strong>S/. ${Number(prod.precio || 0).toFixed(2)}</strong></td>
                <td><span class="stock-badge ${stockClass}">${stockText}</span></td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>
                    <button class="btn-icon edit" onclick="abrirModalEditarProducto(${prod.id})" title="Editar">✏️</button>
                    <button class="btn-icon delete" onclick="abrirModalEliminarProducto(${prod.id})" title="Eliminar">🗑️</button>
                </td>
            </tr>`;
    });
    
    updateProductStats(lista);
}

function updateProductStats(lista) {
    const el = (id) => document.getElementById(id);
    if (el('totalProductosCatalogo')) el('totalProductosCatalogo').textContent = lista.length;
    if (el('productosActivos')) el('productosActivos').textContent = lista.filter(p => p.activo !== false).length;
    if (el('productosInactivos')) el('productosInactivos').textContent = lista.filter(p => p.activo === false).length;
    if (el('totalCategorias')) {
        const cats = [...new Set(lista.map(p => p.categoria).filter(Boolean))];
        el('totalCategorias').textContent = cats.length;
    }
}

function filterProductos() {
    const search = (document.getElementById('searchProducto')?.value || '').toLowerCase();
    const cat = document.getElementById('filterCategoria')?.value || '';
    
    let filtrados = productos;
    if (search) filtrados = filtrados.filter(p => p.nombre.toLowerCase().includes(search) || (p.codigo && p.codigo.toLowerCase().includes(search)));
    if (cat) filtrados = filtrados.filter(p => p.categoria === cat);
    
    renderProductos(filtrados);
}

// ============================================
// PRODUCTOS - AGREGAR/EDITAR
// ============================================

function abrirModalNuevoProducto() {
    console.log('📦 Abriendo modal para nuevo producto');
    productoEditandoId = null;
    
    document.getElementById('modalProductoTitulo').textContent = '📦 Agregar Nuevo Producto';
    document.getElementById('codigoProducto').value = 'TECH-' + String(Date.now()).slice(-6);
    document.getElementById('nombreProducto').value = '';
    document.getElementById('categoriaProducto').value = '';
    document.getElementById('precioProducto').value = '';
    document.getElementById('stockProducto').value = '';
    document.getElementById('stockMinimoProducto').value = '5';
    document.getElementById('descripcionProducto').value = '';
    document.getElementById('imagenProducto').value = '';
    document.getElementById('productoActivo').checked = true;
    
    document.getElementById('modalProducto').style.display = 'flex';
}

function abrirModalEditarProducto(id) {
    console.log('✏️ Editando producto:', id);
    const prod = productos.find(p => p.id === id);
    if (!prod) { alert('Producto no encontrado'); return; }
    
    productoEditandoId = id;
    document.getElementById('modalProductoTitulo').textContent = '✏️ Editar Producto';
    document.getElementById('codigoProducto').value = prod.codigo || '';
    document.getElementById('nombreProducto').value = prod.nombre || '';
    document.getElementById('categoriaProducto').value = prod.categoria || '';
    document.getElementById('precioProducto').value = prod.precio || '';
    document.getElementById('stockProducto').value = prod.stock || 0;
    document.getElementById('stockMinimoProducto').value = prod.stockMinimo || 5;
    document.getElementById('descripcionProducto').value = prod.descripcion || '';
    document.getElementById('imagenProducto').value = prod.imagen || '';
    document.getElementById('productoActivo').checked = prod.activo !== false;
    
    document.getElementById('modalProducto').style.display = 'flex';
}

function cerrarModalProducto() {
    document.getElementById('modalProducto').style.display = 'none';
    productoEditandoId = null;
}

async function guardarProducto() {
    console.log('💾 Guardando producto...');
    
    const codigo = document.getElementById('codigoProducto').value.trim();
    const nombre = document.getElementById('nombreProducto').value.trim();
    const categoria = document.getElementById('categoriaProducto').value;
    const precio = parseFloat(document.getElementById('precioProducto').value) || 0;
    const stock = parseInt(document.getElementById('stockProducto').value) || 0;
    const stockMinimo = parseInt(document.getElementById('stockMinimoProducto').value) || 5;
    const descripcion = document.getElementById('descripcionProducto').value.trim();
    const imagen = document.getElementById('imagenProducto').value.trim();
    const activo = document.getElementById('productoActivo').checked;
    
    // Validaciones
    if (!nombre) { alert('❌ El nombre es requerido'); return; }
    if (!categoria) { alert('❌ La categoría es requerida'); return; }
    if (precio <= 0) { alert('❌ El precio debe ser mayor a 0'); return; }
    
    const data = { codigo, nombre, categoria, precio, stock, stockMinimo, descripcion, imagen, activo };
    console.log('📤 Enviando datos:', data);
    
    try {
        let url = '/api/inventario/productos';
        let method = 'POST';
        
        if (productoEditandoId) {
            url = `/api/inventario/productos/${productoEditandoId}`;
            method = 'PUT';
        }
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        console.log('📥 Respuesta:', response.status);
        
        if (response.ok) {
            const result = await response.json();
            console.log('✅ Producto guardado:', result);
            cerrarModalProducto();
            await loadProductos();
            mostrarExito(productoEditandoId ? '¡Producto Actualizado!' : '¡Producto Agregado!', `"${nombre}" guardado correctamente.`);
        } else {
            const errorText = await response.text();
            console.error('❌ Error del servidor:', errorText);
            alert('❌ Error al guardar: ' + errorText);
        }
    } catch (error) {
        console.error('❌ Error de red:', error);
        alert('❌ Error de conexión: ' + error.message);
    }
}

// ============================================
// PRODUCTOS - ELIMINAR
// ============================================

function abrirModalEliminarProducto(id) {
    console.log('🗑️ Abriendo modal eliminar para:', id);
    const prod = productos.find(p => p.id === id);
    if (!prod) { alert('Producto no encontrado'); return; }
    
    productoEliminarId = id;
    document.getElementById('productoEliminarNombre').textContent = prod.nombre;
    document.getElementById('modalEliminar').style.display = 'flex';
}

function cerrarModalEliminar() {
    document.getElementById('modalEliminar').style.display = 'none';
    productoEliminarId = null;
}

async function confirmarEliminar() {
    if (!productoEliminarId) return;
    
    const prod = productos.find(p => p.id === productoEliminarId);
    const nombre = prod ? prod.nombre : 'Producto';
    
    console.log('🗑️ Eliminando producto:', productoEliminarId);
    
    try {
        const response = await fetch(`/api/inventario/productos/${productoEliminarId}`, { 
            method: 'DELETE'
        });
        
        console.log('📥 Respuesta eliminar:', response.status);
        
        if (response.ok) {
            console.log('✅ Producto eliminado');
            cerrarModalEliminar();
            await loadProductos();
            mostrarExito('¡Producto Eliminado!', `"${nombre}" eliminado del inventario.`);
        } else {
            const errorText = await response.text();
            console.error('❌ Error eliminando:', errorText);
            alert('❌ Error al eliminar: ' + errorText);
        }
    } catch (error) {
        console.error('❌ Error de red:', error);
        alert('❌ Error de conexión: ' + error.message);
    }
}

// ============================================
// UTILIDADES
// ============================================

function mostrarExito(titulo, mensaje) {
    const modal = document.getElementById('modalExito');
    if (modal) {
        document.getElementById('exitoTitulo').textContent = titulo;
        document.getElementById('exitoMensaje').textContent = mensaje;
        modal.style.display = 'flex';
    } else {
        alert(titulo + '\n' + mensaje);
    }
}

function cerrarModalExito() {
    const modal = document.getElementById('modalExito');
    if (modal) modal.style.display = 'none';
}

// Cerrar modales con click fuera o Escape
document.addEventListener('click', e => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.style.display = 'none';
    }
});

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay').forEach(m => m.style.display = 'none');
    }
});

console.log('✅ admin.js cargado completamente');
