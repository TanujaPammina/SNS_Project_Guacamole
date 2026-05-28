<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Historical Logs — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Historical Session Logs</span>
      <span class="topbar-user">${sessionScope.loggedInUser}</span>
    </div>
    <div class="content">

      <!-- Filters -->
      <div class="card">
        <div class="card-header"><h2>Filters</h2></div>
        <div class="card-body">
          <form method="get" action="${pageContext.request.contextPath}/reports">
            <input type="hidden" name="type" value="historical-logs">
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

      <!-- Results -->
      <div class="card">
        <div class="card-header">
          <h2>Session History</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Connection</th>
                <th>Client IP</th>
                <th>Start</th>
                <th>End</th>
                <th>Duration</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty sessions}">
                  <tr><td colspan="6">
                    <div class="empty-state"><p>No sessions found for the selected filters.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="s" items="${sessions}">
                    <tr>
                      <td>${s.username}</td>
                      <td>${s.connectionName}</td>
                      <td><code>${s.remoteHost}</code></td>
                      <td>${s.startDate}</td>
                      <td>${s.endDate}</td>
                      <td>${s.durationFormatted}</td>
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
