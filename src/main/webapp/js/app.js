/**
 * Guacamole Admin — Frontend JS
 * Pure vanilla JS, no frameworks.
 */

'use strict';

// ── Auto-refresh for Active Sessions page ────────────────────────────────────
(function initAutoRefresh() {
  const indicator = document.getElementById('refresh-indicator');
  if (!indicator) return;

  let countdown = 30;

  function tick() {
    indicator.textContent = 'Refreshing in ' + countdown + 's';
    if (countdown <= 0) {
      window.location.reload();
    }
    countdown--;
  }

  tick();
  setInterval(tick, 1000);
})();

// ── Table search / filter ────────────────────────────────────────────────────
(function initTableSearch() {
  const input = document.getElementById('table-search');
  if (!input) return;

  const table = document.querySelector('table tbody');
  if (!table) return;

  input.addEventListener('input', function () {
    const query = this.value.toLowerCase();
    Array.from(table.rows).forEach(function (row) {
      const text = row.textContent.toLowerCase();
      row.style.display = text.includes(query) ? '' : 'none';
    });
  });
})();

// ── Confirm dangerous actions ────────────────────────────────────────────────
document.addEventListener('click', function (e) {
  const btn = e.target.closest('[data-confirm]');
  if (!btn) return;
  if (!window.confirm(btn.dataset.confirm)) {
    e.preventDefault();
  }
});

// ── Highlight active sidebar link ────────────────────────────────────────────
(function highlightNav() {
  const path = window.location.pathname + window.location.search;
  document.querySelectorAll('.sidebar nav a').forEach(function (link) {
    const href = link.getAttribute('href');
    if (href && path.includes(href.split('?')[0]) &&
        (link.href === window.location.href ||
         (href.includes('type=') && path.includes(href.split('type=')[1])))) {
      link.classList.add('active');
    }
  });
})();

// ── Date range quick-select ──────────────────────────────────────────────────
(function initDateShortcuts() {
  document.querySelectorAll('[data-days]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const days = parseInt(this.dataset.days, 10);
      const to   = new Date();
      const from = new Date();
      from.setDate(from.getDate() - days);

      const fmt = function (d) {
        return d.toISOString().split('T')[0];
      };

      const fromInput = document.getElementById('from');
      const toInput   = document.getElementById('to');
      if (fromInput) fromInput.value = fmt(from);
      if (toInput)   toInput.value   = fmt(to);
    });
  });
})();
