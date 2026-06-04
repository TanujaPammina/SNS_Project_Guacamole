<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Failed Logins — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="Failed Login Attempts"/></jsp:include>
    <div class="content">

      <!-- Summary cards -->
      <c:if test="${not empty summary}">
        <div class="card" style="margin-bottom:20px">
          <div class="card-header"><h2>30-Day Summary by User</h2></div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr><th>Username</th><th>Failed Attempts (30d)</th><th>Risk</th></tr>
              </thead>
              <tbody>
                <c:forEach var="f" items="${summary}">
                  <tr>
                    <td>${f.username}</td>
                    <td><strong>${f.failCount}</strong></td>
                    <td>
                      <c:choose>
                        <c:when test="${f.failCount >= 10}"><span class="badge badge-danger">High</span></c:when>
                        <c:when test="${f.failCount >= 5}"><span class="badge badge-warning">Medium</span></c:when>
                        <c:otherwise><span class="badge badge-info">Low</span></c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </c:if>

      <!-- Filters -->
      <div class="card">
        <div class="card-header"><h2>Filters</h2></div>
        <div class="card-body">
          <form method="get" action="${pageContext.request.contextPath}/reports">
            <input type="hidden" name="type" value="failed-logins">
            <div class="filter-bar">
              <label>Username
                <input type="text" name="username" value="${param.username}" placeholder="All users">
              </label>
              <label>From
                <input type="date" id="from" name="from" value="${param.from}">
              </label>
              <label>To
                <input type="date" id="to" name="to" value="${param.to}">
              </label>
              <div style="display:flex;gap:6px;align-self:flex-end">
                <button type="button" class="btn btn-outline" data-days="7">Last 7d</button>
                <button type="button" class="btn btn-outline" data-days="30">Last 30d</button>
                <button type="submit" class="btn btn-primary">Apply</button>
              </div>
            </div>
          </form>
        </div>
      </div>

      <!-- Detail table -->
      <div class="card">
        <div class="card-header">
          <h2>Failed Login Events</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Client IP</th>
                <th>Attempt Time</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty failedLogins}">
                  <tr><td colspan="3">
                    <div class="empty-state"><p>No failed login events found.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="f" items="${failedLogins}">
                    <tr>
                      <td><strong>${f.username}</strong></td>
                      <td><code>${f.remoteIp}</code></td>
                      <td>${f.attemptTime}</td>
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
