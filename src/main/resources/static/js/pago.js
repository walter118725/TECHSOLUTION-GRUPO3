(() => {
  const form = document.getElementById('pagoForm');
  const montoEl = document.getElementById('monto');
  const refEl = document.getElementById('referencia');
  const pasarelaEl = document.getElementById('pasarela');
  const btnProcesar = document.getElementById('btnProcesar');
  const resultadoEl = document.getElementById('resultado');
  const estadoCont = document.getElementById('estadoPasarelas');
  const btnRefrescar = document.getElementById('btnRefrescarEstado');

  const showResult = (type, message) => {
    resultadoEl.style.display = 'block';
    resultadoEl.className = `alert ${type === 'ok' ? 'alert-success' : 'alert-danger'}`;
    resultadoEl.textContent = message;
    setTimeout(() => { resultadoEl.style.display = 'none'; }, 50);
  };

  const fetchEstados = async () => {
    try {
      const res = await fetch('/api/pagos/pasarelas/estado');
      if (!res.ok) throw new Error('No se pudo obtener estados');
      const data = await res.json();
      const entries = Object.entries(data);
      estadoCont.innerHTML = entries.map(([nombre, habilitada]) => `
        <div class="card" style="padding:12px;">
          <div class="flex-between">
            <strong>${nombre}</strong>
            <span class="badge ${habilitada ? 'badge-success' : 'badge-warning'}">${habilitada ? 'Habilitada' : 'Deshabilitada'}</span>
          </div>
          <div class="flex-between" style="margin-top:8px;">
            <button class="btn btn-secondary" data-toggle="${nombre}">${habilitada ? 'Deshabilitar' : 'Habilitar'}</button>
          </div>
        </div>
      `).join('');
    } catch (e) {
      showResult('err', e.message);
    }
  };

  const togglePasarela = async (nombre, habilitar) => {
    try {
      const res = await fetch(`/api/pagos/pasarelas/${nombre}/configurar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ habilitar })
      });
      if (!res.ok) throw new Error('No se pudo actualizar la pasarela');
      await fetchEstados();
      showResult('ok', `Pasarela ${nombre} ${habilitar ? 'habilitada' : 'deshabilitada'} correctamente.`);
    } catch (e) {
      showResult('err', e.message);
    }
  };

  estadoCont?.addEventListener('click', (ev) => {
    const btn = ev.target.closest('button[data-toggle]');
    if (!btn) return;
    const nombre = btn.getAttribute('data-toggle');
    const current = btn.textContent.includes('Deshabilitar');
    togglePasarela(nombre, !current);
  });

  btnRefrescar?.addEventListener('click', fetchEstados);

  btnProcesar?.addEventListener('click', async () => {
    const monto = parseFloat(montoEl.value);
    const referencia = refEl.value.trim();
    const pasarela = pasarelaEl.value;

    if (!monto || monto <= 0 || !referencia) {
      form.classList.add('form-shake');
      setTimeout(() => form.classList.remove('form-shake'), 600);
      showResult('err', 'Completa monto y referencia válidos.');
      return;
    }

    try {
      const res = await fetch('/api/pagos/procesar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pasarela, monto, referencia })
      });
      const data = await res.json();
      if (res.ok && data?.mensaje) {
        showResult('ok', data.mensaje);
      } else {
        showResult('err', data?.mensaje || 'Error al procesar pago');
      }
    } catch (e) {
      showResult('err', e.message);
    }
  });

  // Verificación de pago por referencia (opcional): si hay ?ref=..., consulta estado.
  const params = new URLSearchParams(window.location.search);
  const refQ = params.get('ref');
  const pasQ = params.get('pas');
  if (refQ && pasQ) {
    (async () => {
      try {
        const res = await fetch(`/api/pagos/verificar/${pasQ}/${encodeURIComponent(refQ)}`);
        const data = await res.json();
        if (res.ok && data?.mensaje) {
          showResult('ok', data.mensaje);
        } else {
          showResult('err', data?.mensaje || 'No se pudo verificar');
        }
      } catch (e) {
        showResult('err', e.message);
      }
    })();
  }

  // Inicial
  fetchEstados();
})();
