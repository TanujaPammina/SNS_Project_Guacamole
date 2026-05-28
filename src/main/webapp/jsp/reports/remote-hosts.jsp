<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Remote Hosts — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Remote Host Report</span>
      <span class="topbar-user">${sessionScope.loggedInUser}</span>
    </div>
    <div class="content">

      <div class="alert alert-info">
        Shows which client machines (IP addresses) users are connecting from.
      </div>

      <div class="card">
        <div class="card-header">
          <h2>Client IP Addresses</h2>
          <input id="table-search" type="search" placeholder="Search IP or user…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Client IP</th>
                <th>Username</th>
                <th>Session Count</th>
                <th>Last Seen</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty sessions}">
                  <tr><td colspan="4">
                    <div class="empty-state"><p>No remote host data available.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="s" items="${sessions}">
                    <tr>
                      <td><code>${s.remoteHost}</code></td>
                      <td><a href="${pageContext.request.contextPath}/users?name=${s.username}">${s.username}</a></td>
                      <td>${s.durationSeconds}</td><%-- repurposed as session count --%>
                      <td>${s.startDate}</td>
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
