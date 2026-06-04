<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Top Users — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="Top Users by Sessions"/></jsp:include>
    <div class="content">
      <div class="card">
        <div class="card-header">
          <h2>Top 20 Users</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Username</th>
                <th>Total Sessions</th>
                <th>Total Duration</th>
                <th>Last Seen</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty userStats}">
                  <tr><td colspan="5">
                    <div class="empty-state"><p>No session data available.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="u" items="${userStats}" varStatus="s">
                    <tr>
                      <td><strong>#${s.index + 1}</strong></td>
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
    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
