// ===== DASHBOARD GERENTE - JAVASCRIPT =====

// ===== DATOS DE EJEMPLO (En producción vendrían del backend) =====
const datosVentas = {
    semana: {
        totalVentas: 45,
        ingresos: 12500.00,
        promedio: 277.78,
        clientes: 38,
        ventas: [
            { numero: 'VTA-2025-0045', fecha: '29/11/2025', cliente: 'Juan Pérez', productos: 3, total: 850.00, estado: 'completada' },
            { numero: 'VTA-2025-0044', fecha: '29/11/2025', cliente: 'María García', productos: 1, total: 1200.00, estado: 'completada' },
            { numero: 'VTA-2025-0043', fecha: '28/11/2025', cliente: 'Carlos López', productos: 5, total: 450.00, estado: 'completada' },
            { numero: 'VTA-2025-0042', fecha: '28/11/2025', cliente: 'Ana Torres', productos: 2, total: 680.00, estado: 'pendiente' },
            { numero: 'VTA-2025-0041', fecha: '27/11/2025', cliente: 'Roberto Díaz', productos: 4, total: 1500.00, estado: 'completada' },
        ]
    },
    mes: {
        totalVentas: 186,
        ingresos: 52300.00,
        promedio: 281.18,
        clientes: 142,
        ventas: []
    },
    año: {
        totalVentas: 2150,
        ingresos: 625000.00,
        promedio: 290.70,
        clientes: 1580,
        ventas: []
    }
};

const datosFinanzas = {
    semana: {
        ingresosBrutos: 15200.00,
        gastos: 8500.00,
        utilidadNeta: 6700.00,
        margen: 44
    },
    mes: {
        ingresosBrutos: 103700.00,
        gastos: 74300.00,
        utilidadNeta: 29400.00,
        margen: 28
    },
    año: {
        ingresosBrutos: 845000.00,
        gastos: 582000.00,
        utilidadNeta: 263000.00,
        margen: 31
    }
};

const inventarioProductos = [
    { codigo: 'TECH-001', nombre: 'Laptop HP ProBook 450', categoria: 'Computadoras', stock: 15, precio: 2500.00, emoji: '💻' },
    { codigo: 'TECH-002', nombre: 'Mouse Inalámbrico Logitech', categoria: 'Accesorios', stock: 2, precio: 85.00, emoji: '🖱️' },
    { codigo: 'TECH-003', nombre: 'Teclado Mecánico RGB', categoria: 'Accesorios', stock: 8, precio: 150.00, emoji: '⌨️' },
    { codigo: 'TECH-004', nombre: 'Monitor Samsung 27"', categoria: 'Monitores', stock: 1, precio: 850.00, emoji: '🖥️' },
    { codigo: 'TECH-005', nombre: 'Audífonos Sony WH-1000', categoria: 'Audio', stock: 5, precio: 450.00, emoji: '🎧' },
    { codigo: 'TECH-006', nombre: 'Webcam Logitech C920', categoria: 'Accesorios', stock: 0, precio: 180.00, emoji: '📷' },
    { codigo: 'TECH-007', nombre: 'SSD Samsung 1TB', categoria: 'Almacenamiento', stock: 12, precio: 280.00, emoji: '💾' },
    { codigo: 'TECH-008', nombre: 'RAM DDR4 16GB', categoria: 'Componentes', stock: 3, precio: 120.00, emoji: '🔧' },
    { codigo: 'TECH-009', nombre: 'Cable HDMI 2.0', categoria: 'Accesorios', stock: 25, precio: 25.00, emoji: '🔌' },
    { codigo: 'TECH-010', nombre: 'Impresora HP LaserJet', categoria: 'Impresoras', stock: 2, precio: 650.00, emoji: '🖨️' },
    { codigo: 'TECH-011', nombre: 'Tablet Samsung Galaxy Tab', categoria: 'Tablets', stock: 4, precio: 890.00, emoji: '📱' },
    { codigo: 'TECH-012', nombre: 'Cargador Universal USB-C', categoria: 'Accesorios', stock: 1, precio: 45.00, emoji: '🔋' },
];

// Variable para el producto seleccionado
let productoSeleccionado = null;

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Inicializando Dashboard del Gerente...');
    
    // Mostrar fecha actual
    mostrarFechaActual();
    
    // Cargar datos iniciales
    filterVentas('semana');
    filterFinanzas('semana');
    cargarInventario();
    
    // Configurar búsqueda de inventario
    const searchInput = document.getElementById('searchInventario');
    if (searchInput) {
        searchInput.addEventListener('input', filterInventario);
    }
    
    console.log('✅ Dashboard del Gerente inicializado');
});

// ===== MOSTRAR FECHA ACTUAL =====
function mostrarFechaActual() {
    const fechaElement = document.getElementById('currentDate');
    if (fechaElement) {
        const opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        const fecha = new Date().toLocaleDateString('es-PE', opciones);
        fechaElement.textContent = `📅 ${fecha.charAt(0).toUpperCase() + fecha.slice(1)}`;
    }
}

// ===== NAVEGACIÓN DE TABS =====
function showTab(tabId) {
    // Ocultar todos los contenidos
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Desactivar todos los botones
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Mostrar tab seleccionado
    const tabContent = document.getElementById(`tab-${tabId}`);
    if (tabContent) {
        tabContent.classList.add('active');
    }
    
    // Activar botón seleccionado
    const tabBtn = document.querySelector(`[data-tab="${tabId}"]`);
    if (tabBtn) {
        tabBtn.classList.add('active');
    }
}

// ===== FILTRAR VENTAS POR PERÍODO =====
function filterVentas(periodo) {
    // Actualizar botones
    document.querySelectorAll('#tab-ventas .period-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`#tab-ventas [data-period="${periodo}"]`)?.classList.add('active');
    
    // Obtener datos
    const datos = datosVentas[periodo];
    
    // Actualizar estadísticas con animación
    animateValue('totalVentas', datos.totalVentas);
    document.getElementById('totalIngresos').textContent = `S/. ${datos.ingresos.toLocaleString('es-PE', { minimumFractionDigits: 2 })}`;
    document.getElementById('promedioVenta').textContent = `S/. ${datos.promedio.toLocaleString('es-PE', { minimumFractionDigits: 2 })}`;
    animateValue('clientesAtendidos', datos.clientes);
    
    // Actualizar tabla de ventas
    renderVentasTable(datos.ventas);
}

// ===== RENDERIZAR TABLA DE VENTAS =====
function renderVentasTable(ventas) {
    const tbody = document.getElementById('ventasTableBody');
    if (!tbody) return;
    
    if (ventas.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: #64748b;">
                    📊 No hay ventas para mostrar en este período
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = ventas.map((venta, index) => `
        <tr style="animation: slideIn 0.3s ease ${index * 0.05}s both;">
            <td><strong>${venta.numero}</strong></td>
            <td>${venta.fecha}</td>
            <td>${venta.cliente}</td>
            <td>${venta.productos} productos</td>
            <td><strong>S/. ${venta.total.toLocaleString('es-PE', { minimumFractionDigits: 2 })}</strong></td>
            <td><span class="status-badge ${venta.estado}">${venta.estado.charAt(0).toUpperCase() + venta.estado.slice(1)}</span></td>
        </tr>
    `).join('');
}

// ===== FILTRAR FINANZAS POR PERÍODO =====
function filterFinanzas(periodo) {
    // Actualizar botones
    document.querySelectorAll('#tab-finanzas .period-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`#tab-finanzas [data-period="${periodo}"]`)?.classList.add('active');
    
    // Obtener datos
    const datos = datosFinanzas[periodo];
    
    // Actualizar estadísticas
    document.getElementById('ingresosBrutos').textContent = `S/. ${datos.ingresosBrutos.toLocaleString('es-PE', { minimumFractionDigits: 2 })}`;
    document.getElementById('gastosTotal').textContent = `S/. ${datos.gastos.toLocaleString('es-PE', { minimumFractionDigits: 2 })}`;
    document.getElementById('utilidadNeta').textContent = `S/. ${datos.utilidadNeta.toLocaleString('es-PE', { minimumFractionDigits: 2 })}`;
    document.getElementById('margenGanancia').textContent = `${datos.margen}%`;
}

// ===== CARGAR INVENTARIO =====
function cargarInventario() {
    // Calcular estadísticas
    const total = inventarioProductos.length;
    const stockNormal = inventarioProductos.filter(p => p.stock > 5).length;
    const stockBajo = inventarioProductos.filter(p => p.stock > 2 && p.stock <= 5).length;
    const stockCritico = inventarioProductos.filter(p => p.stock <= 2).length;
    
    // Actualizar cards
    animateValue('totalProductos', total);
    animateValue('stockNormal', stockNormal);
    animateValue('stockBajo', stockBajo);
    animateValue('stockCritico', stockCritico);
    
    // Renderizar alertas
    renderAlertasStock();
    
    // Renderizar tabla
    renderInventarioTable(inventarioProductos);
}

// ===== RENDERIZAR ALERTAS DE STOCK CRÍTICO =====
function renderAlertasStock() {
    const alertasList = document.getElementById('alertasList');
    if (!alertasList) return;
    
    const productosCriticos = inventarioProductos.filter(p => p.stock <= 2);
    
    if (productosCriticos.length === 0) {
        document.getElementById('alertasStock').style.display = 'none';
        return;
    }
    
    document.getElementById('alertasStock').style.display = 'block';
    
    alertasList.innerHTML = productosCriticos.map((producto, index) => `
        <div class="alert-item" style="animation: slideIn 0.3s ease ${index * 0.1}s both;">
            <div class="alert-info">
                <span class="alert-emoji">${producto.emoji}</span>
                <div class="alert-details">
                    <h4>${producto.nombre}</h4>
                    <p>Código: ${producto.codigo} | ${producto.categoria}</p>
                </div>
            </div>
            <div class="alert-stock">
                <div class="stock-number">
                    <span class="value">${producto.stock}</span>
                    <span class="label">unidades</span>
                </div>
                <button class="btn-solicitar" onclick="abrirModalSolicitud('${producto.codigo}')">
                    📦 Solicitar Mercadería
                </button>
            </div>
        </div>
    `).join('');
}

// ===== RENDERIZAR TABLA DE INVENTARIO =====
function renderInventarioTable(productos) {
    const tbody = document.getElementById('inventarioTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = productos.map((producto, index) => {
        let stockClass = 'normal';
        let stockText = 'Normal';
        
        if (producto.stock <= 2) {
            stockClass = 'critico';
            stockText = '🚨 Crítico';
        } else if (producto.stock <= 5) {
            stockClass = 'bajo';
            stockText = '⚠️ Bajo';
        }
        
        return `
            <tr style="animation: slideIn 0.3s ease ${index * 0.03}s both;">
                <td><code>${producto.codigo}</code></td>
                <td>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <span style="font-size: 24px;">${producto.emoji}</span>
                        <span>${producto.nombre}</span>
                    </div>
                </td>
                <td>${producto.categoria}</td>
                <td><strong>${producto.stock}</strong> unidades</td>
                <td><span class="stock-badge ${stockClass}">${stockText}</span></td>
                <td>S/. ${producto.precio.toLocaleString('es-PE', { minimumFractionDigits: 2 })}</td>
                <td>
                    ${producto.stock <= 2 ? `
                        <button class="btn-solicitar" onclick="abrirModalSolicitud('${producto.codigo}')">
                            📦 Solicitar
                        </button>
                    ` : `
                        <button class="btn-solicitar" style="background: #64748b;" onclick="abrirModalSolicitud('${producto.codigo}')">
                            📦 Solicitar
                        </button>
                    `}
                </td>
            </tr>
        `;
    }).join('');
}

// ===== FILTRAR INVENTARIO =====
function filterInventario() {
    const searchText = document.getElementById('searchInventario')?.value.toLowerCase() || '';
    const stockFilter = document.getElementById('filterStock')?.value || 'todos';
    
    let productosFiltrados = inventarioProductos;
    
    // Filtrar por búsqueda
    if (searchText) {
        productosFiltrados = productosFiltrados.filter(p => 
            p.nombre.toLowerCase().includes(searchText) ||
            p.codigo.toLowerCase().includes(searchText) ||
            p.categoria.toLowerCase().includes(searchText)
        );
    }
    
    // Filtrar por estado de stock
    if (stockFilter === 'normal') {
        productosFiltrados = productosFiltrados.filter(p => p.stock > 5);
    } else if (stockFilter === 'bajo') {
        productosFiltrados = productosFiltrados.filter(p => p.stock > 2 && p.stock <= 5);
    } else if (stockFilter === 'critico') {
        productosFiltrados = productosFiltrados.filter(p => p.stock <= 2);
    }
    
    renderInventarioTable(productosFiltrados);
}

// ===== ABRIR MODAL DE SOLICITUD =====
function abrirModalSolicitud(codigo) {
    productoSeleccionado = inventarioProductos.find(p => p.codigo === codigo);
    if (!productoSeleccionado) return;
    
    // Llenar datos del modal
    document.getElementById('solicitudEmoji').textContent = productoSeleccionado.emoji;
    document.getElementById('solicitudNombre').textContent = productoSeleccionado.nombre;
    document.getElementById('solicitudCodigo').textContent = `Código: ${productoSeleccionado.codigo}`;
    document.getElementById('solicitudStockActual').textContent = productoSeleccionado.stock;
    
    // Mostrar modal
    document.getElementById('modalSolicitud').style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

// ===== CERRAR MODAL DE SOLICITUD =====
function cerrarModalSolicitud() {
    document.getElementById('modalSolicitud').style.display = 'none';
    document.body.style.overflow = 'auto';
    productoSeleccionado = null;
}

// ===== ENVIAR SOLICITUD =====
function enviarSolicitud() {
    if (!productoSeleccionado) return;
    
    const cantidad = document.getElementById('cantidadSolicitar').value;
    const proveedor = document.getElementById('proveedorSelect');
    const urgencia = document.getElementById('urgenciaSolicitud');
    
    // Generar número de solicitud
    const numSolicitud = `SOL-2025-${String(Math.floor(Math.random() * 100000)).padStart(5, '0')}`;
    
    // Tiempo estimado según urgencia
    const tiempos = {
        'normal': '5-7 días hábiles',
        'urgente': '2-3 días hábiles',
        'express': '24 horas'
    };
    
    // Cerrar modal de solicitud
    cerrarModalSolicitud();
    
    // Mostrar modal de confirmación
    document.getElementById('numSolicitud').textContent = numSolicitud;
    document.getElementById('confirmProducto').textContent = productoSeleccionado.nombre;
    document.getElementById('confirmCantidad').textContent = `${cantidad} unidades`;
    document.getElementById('confirmTiempo').textContent = tiempos[urgencia.value];
    
    document.getElementById('modalConfirmacion').style.display = 'flex';
    document.body.style.overflow = 'hidden';
    
    console.log('📤 Solicitud enviada:', {
        numero: numSolicitud,
        producto: productoSeleccionado.codigo,
        cantidad: cantidad,
        proveedor: proveedor.options[proveedor.selectedIndex].text,
        urgencia: urgencia.value
    });
}

// ===== CERRAR MODAL DE CONFIRMACIÓN =====
function cerrarModalConfirmacion() {
    document.getElementById('modalConfirmacion').style.display = 'none';
    document.body.style.overflow = 'auto';
}

// ===== EXPORTAR VENTAS =====
function exportarVentas() {
    alert('📥 Descargando reporte de ventas en Excel...\n\nEsta funcionalidad generaría un archivo Excel con todas las ventas del período seleccionado.');
}

// ===== EXPORTAR REPORTE FINANCIERO =====
function exportarReporteFinanciero() {
    alert('📥 Descargando reporte financiero en PDF...\n\nEsta funcionalidad generaría un PDF completo con el análisis financiero.');
}

// ===== ANIMAR VALORES =====
function animateValue(elementId, targetValue) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const startValue = 0;
    const duration = 1000;
    const startTime = performance.now();
    
    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const easeProgress = 1 - Math.pow(1 - progress, 3); // Ease out cubic
        
        const currentValue = Math.floor(startValue + (targetValue - startValue) * easeProgress);
        element.textContent = currentValue;
        
        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }
    
    requestAnimationFrame(update);
}

// Cerrar modales al hacer clic fuera
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        cerrarModalSolicitud();
        cerrarModalConfirmacion();
    }
});

// Cerrar modales con Escape
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        cerrarModalSolicitud();
        cerrarModalConfirmacion();
    }
});
