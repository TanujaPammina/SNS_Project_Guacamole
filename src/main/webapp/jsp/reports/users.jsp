<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Users — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="User Details"/></jsp:include>
    <div class="content">

      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
      </c:if>

      <div class="card">
        <div class="card-header">
          <h2>All Users</h2>
          <input id="table-search" type="search" placeholder="Search users…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Status</th>
                <th>Last Active</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty users}">
                  <tr><td colspan="6">
                    <div class="empty-state"><p>No users found.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="u" items="${users}">
                    <tr>
                      <td><strong>${u.username}</strong></td>
                      <td>${not empty u.fullName ? u.fullName : '—'}</td>
                      <td>${not empty u.email ? u.email : '—'}</td>
                      <td>
                        <c:choose>
                          <c:when test="${u.disabled}">
                            <span class="badge badge-danger">Disabled</span>
                          </c:when>
                          <c:when test="${u.expired}">
                            <span class="badge badge-warning">Expired</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-success">Active</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${not empty u.lastActive ? u.lastActive : '—'}</td>
                      <td>
                        <a class="btn btn-outline"
                           href="${pageContext.request.contextPath}/users?name=${u.username}"
                           style="padding:4px 10px;font-size:12px">View</a>
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
