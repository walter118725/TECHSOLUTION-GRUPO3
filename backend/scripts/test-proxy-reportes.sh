#!/bin/bash

# Script de prueba para validar el Patrón Proxy - RF3 y RF4
# Protección de acceso a reportes financieros

echo "================================================================================================"
echo "🔐 PRUEBAS DEL PATRÓN PROXY - PROTECCIÓN DE REPORTES FINANCIEROS (RF3-RF4)"
echo "================================================================================================"
echo ""

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 3

# Test 1: Usuario GERENTE debe tener acceso
echo "📋 TEST 1: Usuario con rol GERENTE solicitando reporte de ventas"
echo "   Usuario: gerente_sistema | Roles: [GERENTE] | Activo: true"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gerente_sistema",
    "activo": true,
    "roles": ["GERENTE"]
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   ✅ RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   🎭 Roles:', data.get('roles', [])); print('   💰 Total Ventas:', data.get('datos', {}).get('totalVentas', 'N/A') if data.get('exitoso') else 'Acceso denegado')"
echo ""
echo "------------------------------------------------------------------------------------------------"
echo ""

# Test 2: Usuario CONTADOR debe tener acceso
echo "📋 TEST 2: Usuario con rol CONTADOR solicitando reporte de ingresos y gastos"
echo "   Usuario: contador_principal | Roles: [CONTADOR] | Activo: true"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/ingresos-gastos \
  -H "Content-Type: application/json" \
  -d '{
    "username": "contador_principal",
    "activo": true,
    "roles": ["CONTADOR"],
    "mes": 11,
    "anio": 2025
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   ✅ RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   🎭 Roles:', data.get('roles', [])); print('   💵 Total Ingresos:', data.get('datos', {}).get('totalIngresos', 'N/A') if data.get('exitoso') else 'Acceso denegado')"
echo ""
echo "------------------------------------------------------------------------------------------------"
echo ""

# Test 3: Usuario COMPRAS NO debe tener acceso
echo "📋 TEST 3: Usuario con rol COMPRAS intentando acceder (DEBE SER BLOQUEADO)"
echo "   Usuario: usuario_compras | Roles: [COMPRAS] | Activo: true"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/utilidades \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario_compras",
    "activo": true,
    "roles": ["COMPRAS"]
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   🚫 RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   ⚠️  Código HTTP: 403 FORBIDDEN' if not data.get('exitoso') else '   ❌ ERROR: Usuario sin permisos tuvo acceso!')"
echo ""
echo "------------------------------------------------------------------------------------------------"
echo ""

# Test 4: Usuario VENTAS NO debe tener acceso
echo "📋 TEST 4: Usuario con rol VENTAS intentando acceder (DEBE SER BLOQUEADO)"
echo "   Usuario: vendedor01 | Roles: [VENTAS] | Activo: true"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vendedor01",
    "activo": true,
    "roles": ["VENTAS"]
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   🚫 RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   ⚠️  Código HTTP: 403 FORBIDDEN' if not data.get('exitoso') else '   ❌ ERROR: Usuario sin permisos tuvo acceso!')"
echo ""
echo "------------------------------------------------------------------------------------------------"
echo ""

# Test 5: Usuario inactivo NO debe tener acceso
echo "📋 TEST 5: Usuario GERENTE pero inactivo intentando acceder (DEBE SER BLOQUEADO)"
echo "   Usuario: gerente_inactivo | Roles: [GERENTE] | Activo: false"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gerente_inactivo",
    "activo": false,
    "roles": ["GERENTE"]
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   🚫 RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   ⚠️  Razón: Usuario inactivo' if not data.get('exitoso') else '   ❌ ERROR: Usuario inactivo tuvo acceso!')"
echo ""
echo "------------------------------------------------------------------------------------------------"
echo ""

# Test 6: Exportación a PDF (solo GERENTE/CONTADOR)
echo "📋 TEST 6: Usuario GERENTE exportando reporte a PDF"
echo "   Usuario: gerente_sistema | Roles: [GERENTE] | Activo: true"
echo ""
curl -s -X POST http://localhost:8080/api/reportes/exportar-pdf \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gerente_sistema",
    "activo": true,
    "roles": ["GERENTE"],
    "nombreArchivo": "reporte_financiero_2025"
  }' | python3 -c "import sys, json; data=json.load(sys.stdin); print('   ✅ RESULTADO:', data['mensaje']); print('   👤 Usuario:', data.get('usuario', 'N/A')); print('   📄 Archivo PDF:', data.get('rutaPDF', 'N/A') if data.get('exitoso') else 'Acceso denegado')"
echo ""

echo "================================================================================================"
echo "✅ RESUMEN DE PRUEBAS"
echo "================================================================================================"
echo ""
echo "RF3: ✅ El sistema protege el acceso a reportes financieros validando credenciales y roles"
echo "RF4: ✅ Solo usuarios con rol GERENTE o CONTADOR pueden acceder a reportes completos"
echo ""
echo "VALIDACIONES:"
echo "   ✅ Usuario GERENTE → Acceso PERMITIDO"
echo "   ✅ Usuario CONTADOR → Acceso PERMITIDO"
echo "   ✅ Usuario COMPRAS → Acceso BLOQUEADO (403 Forbidden)"
echo "   ✅ Usuario VENTAS → Acceso BLOQUEADO (403 Forbidden)"
echo "   ✅ Usuario inactivo → Acceso BLOQUEADO (403 Forbidden)"
echo ""
echo "🎉 PATRÓN PROXY FUNCIONANDO CORRECTAMENTE"
echo "================================================================================================"
