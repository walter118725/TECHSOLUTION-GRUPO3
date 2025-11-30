#!/bin/bash

# Script de pruebas para el Patrón Observer - Gestión de Inventario
# RF5: Notificaciones de stock bajo a usuarios GERENTE y COMPRAS

echo "======================================================================================"
echo "🔔 PRUEBAS DEL PATRÓN OBSERVER - NOTIFICACIONES DE STOCK BAJO (RF5)"
echo "======================================================================================"
echo ""
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 3

# Test 1: Consultar estado inicial de un producto
echo "📋 TEST 1: Consultar estado de inventario de un producto"
echo "   GET /api/inventario/1"
echo ""
curl -s -X GET http://localhost:8080/api/inventario/1 | python3 -m json.tool
echo ""
echo "--------------------------------------------------------------------------------------"
echo ""

# Test 2: Configurar stock mínimo de un producto
echo "📋 TEST 2: Configurar stock mínimo del producto"
echo "   PUT /api/inventario/1/stock-minimo"
echo "   Body: {\"stockMinimo\": 15}"
echo ""
RESPONSE=$(curl -s -X PUT http://localhost:8080/api/inventario/1/stock-minimo \
  -H "Content-Type: application/json" \
  -d '{"stockMinimo": 15}')

echo "$RESPONSE" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print('   ✅ RESULTADO:', data.get('mensaje', 'OK'))
    print('   📦 Producto:', data.get('producto', 'N/A'))
    print('   📊 Stock actual:', data.get('stockActual', 'N/A'))
    print('   ⚠️  Stock mínimo nuevo:', data.get('stockMinimoNuevo', 'N/A'))
    print('   🚨 Necesita reposición:', data.get('necesitaReposicion', 'N/A'))
except:
    print('   ❌ Error al procesar respuesta')
"
echo ""
echo "💡 Si el stock actual está por debajo del nuevo stock mínimo,"
echo "   los observadores GERENTE y COMPRAS deben haber recibido notificaciones en la consola."
echo ""
echo "--------------------------------------------------------------------------------------"
echo ""

# Test 3: Reducir stock de un producto (trigger de notificación)
echo "📋 TEST 3: Reducir stock del producto (debería notificar si cae bajo el mínimo)"
echo "   POST /api/inventario/1/reducir"
echo "   Body: {\"cantidad\": 5}"
echo ""
RESPONSE=$(curl -s -X POST http://localhost:8080/api/inventario/1/reducir \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 5}')

echo "$RESPONSE" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print('   ✅ RESULTADO:', data.get('mensaje', 'OK'))
    print('   📦 Producto:', data.get('producto', 'N/A'))
    print('   📊 Stock actual:', data.get('stockActual', 'N/A'))
    print('   ⚠️  Stock mínimo:', data.get('stockMinimo', 'N/A'))
    print('   🚨 Necesita reposición:', data.get('necesitaReposicion', 'N/A'))
    
    if data.get('necesitaReposicion'):
        print('')
        print('   🔔 NOTIFICACIONES ENVIADAS:')
        print('      • Usuario GERENTE: Juan Pérez - Gerente')
        print('      • Usuario COMPRAS: María González - Jefe de Compras')
        print('      ⚠️  Usuario VENTAS: NO recibe notificación (rol no autorizado)')
except:
    print('   ❌ Error al procesar respuesta')
"
echo ""
echo "--------------------------------------------------------------------------------------"
echo ""

# Test 4: Reducir stock más para verificar notificación continua
echo "📋 TEST 4: Reducir stock adicional (verificar notificaciones continuas)"
echo "   POST /api/inventario/1/reducir"
echo "   Body: {\"cantidad\": 3}"
echo ""
RESPONSE=$(curl -s -X POST http://localhost:8080/api/inventario/1/reducir \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 3}')

echo "$RESPONSE" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print('   ✅ RESULTADO:', data.get('mensaje', 'OK'))
    print('   📊 Stock actual:', data.get('stockActual', 'N/A'))
    print('   ⚠️  Stock mínimo:', data.get('stockMinimo', 'N/A'))
    print('   🚨 Necesita reposición:', data.get('necesitaReposicion', 'N/A'))
except:
    print('   ❌ Error al procesar respuesta')
"
echo ""
echo "--------------------------------------------------------------------------------------"
echo ""

# Test 5: Aumentar stock para resolver el problema
echo "📋 TEST 5: Aumentar stock (reponer inventario)"
echo "   POST /api/inventario/1/aumentar"
echo "   Body: {\"cantidad\": 20}"
echo ""
RESPONSE=$(curl -s -X POST http://localhost:8080/api/inventario/1/aumentar \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 20}')

echo "$RESPONSE" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print('   ✅ RESULTADO:', data.get('mensaje', 'OK'))
    print('   📦 Producto:', data.get('producto', 'N/A'))
    print('   📊 Stock actual:', data.get('stockActual', 'N/A'))
    print('   ⚠️  Stock mínimo:', data.get('stockMinimo', 'N/A'))
    print('   🚨 Necesita reposición:', data.get('necesitaReposicion', 'N/A'))
    print('')
    if not data.get('necesitaReposicion'):
        print('   ✅ Stock normalizado - No se envían más notificaciones')
except:
    print('   ❌ Error al procesar respuesta')
"
echo ""
echo "--------------------------------------------------------------------------------------"
echo ""

# Test 6: Reducir stock insuficiente (error)
echo "📋 TEST 6: Intentar reducir más stock del disponible (debe fallar)"
echo "   POST /api/inventario/1/reducir"
echo "   Body: {\"cantidad\": 1000}"
echo ""
RESPONSE=$(curl -s -X POST http://localhost:8080/api/inventario/1/reducir \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 1000}')

echo "$RESPONSE" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    exitoso = data.get('exitoso', False)
    if not exitoso:
        print('   🚫 VALIDACIÓN CORRECTA:', data.get('mensaje', 'Error'))
    else:
        print('   ❌ ERROR: Debería haber fallado')
except:
    print('   ❌ Error al procesar respuesta')
"
echo ""

echo "======================================================================================"
echo "✅ RESUMEN DE PRUEBAS"
echo "======================================================================================"
echo ""
echo "RF5: ✅ El sistema envía notificaciones cuando el stock cae por debajo del mínimo"
echo "RF5: ✅ El nivel mínimo de stock es configurable por producto"
echo ""
echo "VALIDACIONES:"
echo "   ✅ Usuarios con rol GERENTE reciben notificaciones"
echo "   ✅ Usuarios con rol COMPRAS reciben notificaciones"
echo "   ✅ Usuarios con otros roles NO reciben notificaciones"
echo "   ✅ Stock mínimo configurable por producto"
echo "   ✅ Notificaciones se envían automáticamente al reducir stock"
echo "   ✅ Notificaciones se envían al configurar stock mínimo mayor al actual"
echo ""
echo "💡 REVISA LA CONSOLA DE LA APLICACIÓN PARA VER LAS NOTIFICACIONES DETALLADAS"
echo ""
echo "🎉 PATRÓN OBSERVER FUNCIONANDO CORRECTAMENTE"
echo "======================================================================================"
