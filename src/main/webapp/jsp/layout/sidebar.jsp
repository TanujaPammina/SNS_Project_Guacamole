<%-- Role-aware sidebar with configurable report visibility --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Mobile overlay (tap to close sidebar) --%>
<div class="sidebar-overlay" id="sidebar-overlay"></div>

<aside class="sidebar" id="sidebar">
  <div class="sidebar-brand">
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2">
      <rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/>
    </svg>
    Guacamole Admin
  </div>

  <%-- User + role badge --%>
  <div style="padding:8px 16px 12px;border-bottom:1px solid rgba(255,255,255,.08)">
    <span style="font-size:11px;color:rgba(255,255,255,.5)">Signed in as</span><br>
    <span style="font-size:13px;color:#fff;font-weight:600">${sessionScope.currentUser.username}</span>
    <span style="margin-left:6px;font-size:10px;padding:2px 7px;border-radius:10px;
      background:rgba(44,123,229,.35);color:#90c4ff;font-weight:700">
      ${sessionScope.currentUser.roleDisplayName}
    </span>
  </div>

  <div class="sidebar-section">Overview</div>
  <nav>
    <a href="${pageContext.request.contextPath}/dashboard">
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/>
        <rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
      </svg>Dashboard
    </a>
  </nav>

  <%-- Session Reports — shown only if the role has at least one of these permitted --%>
  <c:set var="role" value="${sessionScope.currentUser.role.name()}"/>
  <c:set var="allowedReports" value="${sessionScope.allowedReports}"/>
  <%-- allowedReports is a Set<String> populated by RoleFilter into the session --%>

  <%-- Super Admin always sees everything --%>
  <c:set var="isSuperAdmin" value="${role == 'SUPER_ADMIN'}"/>

  <c:if test="${isSuperAdmin or (not empty allowedReports and
               (allowedReports.contains('active-sessions') or
                allowedReports.contains('historical-logs') or
                allowedReports.contains('session-duration') or
                allowedReports.contains('concurrent-sessions')))}">
    <div class="sidebar-section">Session Reports</div>
    <nav>
      <c:if test="${isSuperAdmin or allowedReports.contains('active-sessions')}">
        <a href="${pageContext.request.contextPath}/reports?type=active-sessions">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
          </svg>Active Sessions
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('historical-logs')}">
        <a href="${pageContext.request.contextPath}/reports?type=historical-logs">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>Historical Logs
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('session-duration')}">
        <a href="${pageContext.request.contextPath}/reports?type=session-duration">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
          </svg>Session Duration
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('concurrent-sessions')}">
        <a href="${pageContext.request.contextPath}/reports?type=concurrent-sessions">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>Concurrent Sessions
        </a>
      </c:if>
    </nav>
  </c:if>

  <c:if test="${isSuperAdmin or (not empty allowedReports and
               (allowedReports.contains('top-users') or
                allowedReports.contains('top-connections')))}">
    <div class="sidebar-section">User Reports</div>
    <nav>
      <a href="${pageContext.request.contextPath}/users">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>User Details
      </a>
      <c:if test="${isSuperAdmin or allowedReports.contains('top-users')}">
        <a href="${pageContext.request.contextPath}/reports?type=top-users">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>Top Users
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('top-connections')}">
        <a href="${pageContext.request.contextPath}/reports?type=top-connections">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="2" y="2" width="20" height="8" rx="2"/><rect x="2" y="14" width="20" height="8" rx="2"/>
          </svg>Top Connections
        </a>
      </c:if>
    </nav>
  </c:if>

  <c:if test="${isSuperAdmin or (not empty allowedReports and
               (allowedReports.contains('failed-logins') or
                allowedReports.contains('remote-hosts') or
                allowedReports.contains('after-hours')))}">
    <div class="sidebar-section">Security Reports</div>
    <nav>
      <c:if test="${isSuperAdmin or allowedReports.contains('failed-logins')}">
        <a href="${pageContext.request.contextPath}/reports?type=failed-logins">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>Failed Logins
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('remote-hosts')}">
        <a href="${pageContext.request.contextPath}/reports?type=remote-hosts">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="2" y1="12" x2="22" y2="12"/>
            <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
          </svg>Remote Hosts
        </a>
      </c:if>
      <c:if test="${isSuperAdmin or allowedReports.contains('after-hours')}">
        <a href="${pageContext.request.contextPath}/reports?type=after-hours">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>After-Hours Access
        </a>
      </c:if>
    </nav>
  </c:if>

  <c:if test="${isSuperAdmin or (not empty allowedReports and allowedReports.contains('audit-log'))}">
    <div class="sidebar-section">Audit</div>
    <nav>
      <a href="${pageContext.request.contextPath}/reports?type=audit-log">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
        </svg>Audit Log
      </a>
    </nav>
  </c:if>

  <c:if test="${role == 'SUPER_ADMIN'}">
    <div class="sidebar-section">Administration</div>
    <nav>
      <a href="${pageContext.request.contextPath}/admin/users">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>Manage Admins
      </a>
      <a href="${pageContext.request.contextPath}/admin/report-permissions">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="3"/>
          <path d="M19.07 4.93a10 10 0 0 1 1.41 13.84M4.93 19.07A10 10 0 0 1 3.52 5.23"/>
          <path d="M12 2v2M12 20v2M2 12h2M20 12h2"/>
        </svg>Report Permissions
      </a>
    </nav>
  </c:if>

  <div class="sidebar-footer">
    <a href="${pageContext.request.contextPath}/profile"
       style="margin-bottom:10px;display:flex;align-items:center;gap:8px;color:var(--sidebar-text);font-size:13px">
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
        <circle cx="12" cy="7" r="4"/>
      </svg>My Profile
    </a>
    <a href="${pageContext.request.contextPath}/logout"
       data-confirm="Are you sure you want to log out?">
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
        <polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
      </svg>Logout
    </a>
  </div>
</aside>
