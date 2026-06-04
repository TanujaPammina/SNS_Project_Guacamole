<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>User Detail — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="User Detail"/></jsp:include>
    <div class="content">

      <c:choose>
        <c:when test="${empty user}">
          <div class="alert alert-danger">User not found.</div>
        </c:when>
        <c:otherwise>
          <div class="card">
            <div class="card-header">
              <h2>${user.username}</h2>
              <c:choose>
                <c:when test="${user.disabled}"><span class="badge badge-danger">Disabled</span></c:when>
                <c:when test="${user.expired}"><span class="badge badge-warning">Expired</span></c:when>
                <c:otherwise><span class="badge badge-success">Active</span></c:otherwise>
              </c:choose>
            </div>
            <div class="card-body">
              <table style="max-width:500px">
                <tbody>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Username</td>
                      <td>${user.username}</td></tr>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Full Name</td>
                      <td>${not empty user.fullName ? user.fullName : '—'}</td></tr>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Email</td>
                      <td>${not empty user.email ? user.email : '—'}</td></tr>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Last Active</td>
                      <td>${not empty user.lastActive ? user.lastActive : '—'}</td></tr>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Disabled</td>
                      <td>${user.disabled}</td></tr>
                  <tr><td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Expired</td>
                      <td>${user.expired}</td></tr>
                </tbody>
              </table>
            </div>
          </div>

          <div style="margin-top:8px">
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/users">← Back to Users</a>
            <a class="btn btn-outline" style="margin-left:8px"
               href="${pageContext.request.contextPath}/reports?type=historical-logs&username=${user.username}">
              View Sessions
            </a>
          </div>
        </c:otherwise>
      </c:choose>

    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
