<%-- After-Hours Access Report --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>After-Hours Access — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="After-Hours Access Report"/></jsp:include>
    <div class="content">

      <div class="alert alert-warning">
        Sessions that started <strong>before 08:00 or after 18:00</strong>, or on
        <strong>weekends</strong>. These may require review.
      </div>

      <div class="card">
        <div class="card-header">
          <h2>After-Hours Sessions</h2>
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
                <th>Start Time</th>
                <th>End Time</th>
                <th>Duration</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty sessions}">
                  <tr><td colspan="7">
                    <div class="empty-state"><p>No after-hours sessions found.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="s" items="${sessions}">
                    <tr>
                      <td><strong>${s.username}</strong></td>
                      <td>${s.connectionName}</td>
                      <td><code>${s.remoteHost}</code></td>
                      <td>${s.startDate}</td>
                      <td>${not empty s.endDate ? s.endDate : '—'}</td>
                      <td>${s.durationFormatted}</td>
                      <td>
                        <c:choose>
                          <c:when test="${empty s.endDate}">
                            <span class="badge badge-success">Active</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-warning">Ended</span>
                          </c:otherwise>
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
