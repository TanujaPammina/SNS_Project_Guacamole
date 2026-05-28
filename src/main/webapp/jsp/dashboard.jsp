<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>

  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Dashboard</span>
      <span class="topbar-user">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        ${sessionScope.currentUser.username}
        <span class="badge badge-info" style="margin-left:4px">${sessionScope.currentUser.roleDisplayName}</span>
      </span>
    </div>

    <div class="content">

      <c:if test="${not empty dbError}">
        <div class="alert alert-danger">${dbError}</div>
      </c:if>

      <!-- Stat Cards -->
      <div class="stat-grid">
        <div class="stat-card success">
          <span class="label">Active Sessions</span>
          <span class="value">${activeSessions}</span>
        </div>
        <div class="stat-card danger">
          <span class="label">Failed Logins (30d)</span>
          <span class="value">${failedLogins30d}</span>
        </div>
        <div class="stat-card warning">
          <span class="label">After-Hours Events</span>
          <span class="value">${afterHoursCount}</span>
        </div>
      </div>

      <!-- Quick links -->
      <div class="card">
        <div class="card-header"><h2>Reports</h2></div>
        <div class="card-body">
          <div class="stat-grid">
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=active-sessions">Active Sessions</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=historical-logs">Historical Logs</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/users">User Details</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=top-users">Top Users</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=session-duration">Session Duration</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=failed-logins">Failed Logins</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=concurrent-sessions">Concurrent Sessions</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=remote-hosts">Remote Hosts</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=after-hours">After-Hours Access</a>
          </div>
        </div>
      </div>

      <!-- Top Users table -->
      <div class="card">
        <div class="card-header">
          <h2>Top Users (Last 30 Days)</h2>
          <a class="btn btn-outline" href="${pageContext.request.contextPath}/reports?type=top-users">View Full Report</a>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Username</th>
                <th>Total Sessions</th>
                <th>Total Duration</th>
                <th>Last Seen</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty topUsers}">
                  <tr><td colspan="5" style="text-align:center;color:#6c757d;padding:24px">No data available</td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="u" items="${topUsers}" varStatus="s">
                    <tr>
                      <td>${s.index + 1}</td>
                      <td><a href="${pageContext.request.contextPath}/users?name=${u.username}">${u.username}</a></td>
                      <td>${u.totalSessions}</td>
                      <td>${u.totalDurationFormatted}</td>
                      <td>${u.lastSeen}</td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </div>

    </div><!-- /content -->
  </div><!-- /main -->
</div><!-- /layout -->
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
