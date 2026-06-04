<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Top Connections — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="Top Connections"/></jsp:include>
    <div class="content">
      <div class="card">
        <div class="card-header">
          <h2>Top 20 Connections by Usage</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Connection Name</th>
                <th>Total Sessions</th>
                <th>Total Duration</th>
                <th>Avg Duration</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty connStats}">
                  <tr><td colspan="5">
                    <div class="empty-state"><p>No connection data available.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="c" items="${connStats}" varStatus="s">
                    <tr>
                      <td><strong>#${s.index + 1}</strong></td>
                      <td>${c.connectionName}</td>
                      <td>${c.totalSessions}</td>
                      <td>${c.totalDurationFormatted}</td>
                      <td>${c.avgDurationFormatted}</td>
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
