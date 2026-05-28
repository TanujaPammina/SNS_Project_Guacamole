<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Active Sessions — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Active Sessions</span>
      <span class="topbar-user">${sessionScope.loggedInUser}</span>
    </div>
    <div class="content">

      <div class="card">
        <div class="card-header">
          <h2>Currently Active Sessions</h2>
          <div style="display:flex;gap:10px;align-items:center">
            <span id="refresh-indicator" style="font-size:12px;color:#6c757d"></span>
            <input id="table-search" type="search" placeholder="Search…"
                   style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
          </div>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Connection</th>
                <th>Client IP</th>
                <th>Started</th>
                <th>Duration</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty sessions}">
                  <tr><td colspan="6">
                    <div class="empty-state"><p>No active sessions at this time.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="s" items="${sessions}">
                    <tr>
                      <td><strong>${s.username}</strong></td>
                      <td>${s.connectionName}</td>
                      <td><code>${s.remoteHost}</code></td>
                      <td>${s.startDate}</td>
                      <td>${s.durationFormatted}</td>
                      <td><span class="badge badge-success">Active</span></td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
