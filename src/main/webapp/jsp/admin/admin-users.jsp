<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Manage Admins — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Manage Admin Users</span>
      <span class="topbar-user">${sessionScope.currentUser.username}</span>
    </div>
    <div class="content">

      <%-- Flash messages --%>
      <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-info">${sessionScope.flashSuccess}</div>
        <c:remove var="flashSuccess" scope="session"/>
      </c:if>
      <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-danger">${sessionScope.flashError}</div>
        <c:remove var="flashError" scope="session"/>
      </c:if>
      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
      </c:if>

      <div class="card">
        <div class="card-header">
          <h2>Admin Accounts</h2>
          <a class="btn btn-primary"
             href="${pageContext.request.contextPath}/admin/users?action=new">
            + New Admin User
          </a>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Last Login</th>
                <th>Created By</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty adminUsers}">
                  <tr><td colspan="8">
                    <div class="empty-state"><p>No admin users found.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="u" items="${adminUsers}">
                    <tr>
                      <td><strong>${u.username}</strong></td>
                      <td>${not empty u.fullName ? u.fullName : '—'}</td>
                      <td>${not empty u.email ? u.email : '—'}</td>
                      <td>
                        <c:choose>
                          <c:when test="${u.role.name() == 'SUPER_ADMIN'}">
                            <span class="badge badge-danger">${u.roleDisplayName}</span>
                          </c:when>
                          <c:when test="${u.role.name() == 'ADMIN'}">
                            <span class="badge badge-info">${u.roleDisplayName}</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-muted">${u.roleDisplayName}</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${u.active}">
                            <span class="badge badge-success">Active</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-danger">Inactive</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${not empty u.lastLoginAt ? u.lastLoginAt : '—'}</td>
                      <td>${not empty u.createdBy ? u.createdBy : '—'}</td>
                      <td>
                        <div style="display:flex;gap:6px">
                          <a class="btn btn-outline"
                             href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.id}"
                             style="padding:4px 10px;font-size:12px">Edit</a>
                          <c:if test="${u.username != sessionScope.currentUser.username}">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/users?action=delete&id=${u.id}"
                                  style="display:inline">
                              <button type="submit" class="btn btn-danger"
                                      style="padding:4px 10px;font-size:12px"
                                      data-confirm="Delete admin user '${u.username}'? This cannot be undone.">
                                Delete
                              </button>
                            </form>
                          </c:if>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Role legend -->
      <div class="card">
        <div class="card-header"><h2>Role Permissions</h2></div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Permission</th>
                <th>Super Admin</th>
                <th>Admin</th>
                <th>Auditor</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>View all reports</td>
                <td><span class="badge badge-success">✓</span></td>
                <td><span class="badge badge-success">✓</span></td>
                <td><span class="badge badge-success">✓</span></td>
              </tr>
              <tr>
                <td>View audit log</td>
                <td><span class="badge badge-success">✓</span></td>
                <td><span class="badge badge-success">✓</span></td>
                <td><span class="badge badge-danger">✗</span></td>
              </tr>
              <tr>
                <td>Manage admin users</td>
                <td><span class="badge badge-success">✓</span></td>
                <td><span class="badge badge-danger">✗</span></td>
                <td><span class="badge badge-danger">✗</span></td>
              </tr>
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
