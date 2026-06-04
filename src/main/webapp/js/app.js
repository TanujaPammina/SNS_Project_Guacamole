'use strict';

// ── Mobile sidebar toggle ────────────────────────────────────────────────────
(function initMobileMenu() {
  const toggle   = document.getElementById('menu-toggle');
  const sidebar  = document.querySelector('.sidebar');
  const overlay  = document.getElementById('sidebar-overlay');

  if (!toggle || !sidebar) return;

  function openSidebar() {
    sidebar.classList.add('open');
    if (overlay) overlay.classList.add('open');
    document.body.style.overflow = 'hidden'; // prevent background scroll
  }

  function closeSidebar() {
    sidebar.classList.remove('open');
    if (overlay) overlay.classList.remove('open');
    document.body.style.overflow = '';
  }

  toggle.addEventListener('click', function () {
    sidebar.classList.contains('open') ? closeSidebar() : openSidebar();
  });

  if (overlay) {
    overlay.addEventListener('click', closeSidebar);
  }

  // Close sidebar when a nav link is tapped on mobile
  sidebar.querySelectorAll('nav a').forEach(function (link) {
    link.addEventListener('click', function () {
      if (window.innerWidth <= 700) closeSidebar();
    });
  });
})();

// ── Auto-refresh for Active Sessions page ────────────────────────────────────
(function initAutoRefresh() {
  const indicator = document.getElementById('refresh-indicator');
  if (!indicator) return;

  let countdown = 30;

  function tick() {
    indicator.textContent = 'Refreshing in ' + countdown + 's';
    if (countdown <= 0) window.location.reload();
    countdown--;
  }

  tick();
  setInterval(tick, 1000);
})();

// ── Table search / filter ────────────────────────────────────────────────────
(function initTableSearch() {
  const input = document.getElementById('table-search');
  if (!input) return;

  const tbody = document.querySelector('table tbody');
  if (!tbody) return;

  input.addEventListener('input', function () {
    const query = this.value.toLowerCase();
    Array.from(tbody.rows).forEach(function (row) {
      row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
    });
  });
})();

// ── Confirm dangerous actions ────────────────────────────────────────────────
document.addEventListener('click', function (e) {
  const btn = e.target.closest('[data-confirm]');
  if (!btn) return;
  if (!window.confirm(btn.dataset.confirm)) e.preventDefault();
});

// ── Highlight active sidebar link ────────────────────────────────────────────
(function highlightNav() {
  const path   = window.location.pathname;
  const search = window.location.search;
  const full   = path + search;

  document.querySelectorAll('.sidebar nav a').forEach(function (link) {
    const href = link.getAttribute('href') || '';
    if (!href || href === '#') return;

    const hPath   = href.split('?')[0];
    const hSearch = href.includes('?') ? href.split('?')[1] : '';

    if (path.endsWith(hPath) && (!hSearch || search.includes(hSearch))) {
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

      const fmt = function (d) { return d.toISOString().split('T')[0]; };

      const fromInput = document.getElementById('from');
      const toInput   = document.getElementById('to');
      if (fromInput) fromInput.value = fmt(from);
      if (toInput)   toInput.value   = fmt(to);
    });
  });
})();
