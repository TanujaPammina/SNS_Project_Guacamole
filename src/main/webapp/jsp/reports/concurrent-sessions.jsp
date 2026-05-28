<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Concurrent Sessions — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Concurrent Sessions Report</span>
      <span class="topbar-user">${sessionScope.loggedInUser}</span>
    </div>
    <div class="content">

      <div class="alert alert-info">
        Shows the peak number of simultaneous sessions recorded per connection.
      </div>

      <div class="card">
        <div class="card-header">
          <h2>Peak Concurrent Sessions per Connection</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Connection Name</th>
                <th>Peak Concurrent Sessions</th>
                <th>Load Level</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty connStats}">
                  <tr><td colspan="3">
                    <div class="empty-state"><p>No concurrent session data available.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="c" items="${connStats}">
                    <tr>
                      <td>${c.connectionName}</td>
                      <td><strong>${c.maxConcurrent}</strong></td>
                      <td>
                        <c:choose>
                          <c:when test="${c.maxConcurrent >= 10}"><span class="badge badge-danger">High</span></c:when>
                          <c:when test="${c.maxConcurrent >= 5}"><span class="badge badge-warning">Medium</span></c:when>
                          <c:otherwise><span class="badge badge-success">Low</span></c:otherwise>
                        </c:choose>
                      </td>
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
