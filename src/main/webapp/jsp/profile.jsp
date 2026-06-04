<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My Profile — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="My Profile"/></jsp:include>
    <div class="content">

      <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;max-width:800px">

        <!-- Profile info card -->
        <div class="card">
          <div class="card-header"><h2>Account Details</h2></div>
          <div class="card-body">
            <table>
              <tbody>
                <tr>
                  <td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d;white-space:nowrap">Username</td>
                  <td><strong>${sessionScope.currentUser.username}</strong></td>
                </tr>
                <tr>
                  <td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Full Name</td>
                  <td>${not empty sessionScope.currentUser.fullName ? sessionScope.currentUser.fullName : '—'}</td>
                </tr>
                <tr>
                  <td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Email</td>
                  <td>${not empty sessionScope.currentUser.email ? sessionScope.currentUser.email : '—'}</td>
                </tr>
                <tr>
                  <td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Role</td>
                  <td>
                    <c:choose>
                      <c:when test="${sessionScope.currentUser.role.name() == 'SUPER_ADMIN'}">
                        <span class="badge badge-danger">${sessionScope.currentUser.roleDisplayName}</span>
                      </c:when>
                      <c:when test="${sessionScope.currentUser.role.name() == 'ADMIN'}">
                        <span class="badge badge-info">${sessionScope.currentUser.roleDisplayName}</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge badge-muted">${sessionScope.currentUser.roleDisplayName}</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
                <tr>
                  <td style="padding:8px 16px 8px 0;font-weight:600;color:#6c757d">Last Login</td>
                  <td>${not empty sessionScope.currentUser.lastLoginAt ? sessionScope.currentUser.lastLoginAt : '—'}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Change password card -->
        <div class="card">
          <div class="card-header"><h2>Change Password</h2></div>
          <div class="card-body">

            <c:if test="${not empty successMessage}">
              <div class="alert alert-info" style="background:#d4f5e9;color:#00875a;border-color:#b7ebd8">
                ✓ ${successMessage}
              </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
              <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/profile">
              <div class="form-group">
                <label for="currentPassword">Current Password</label>
                <input type="password" id="currentPassword" name="currentPassword"
                       required autocomplete="current-password"
                       placeholder="Enter your current password">
              </div>
              <div class="form-group">
                <label for="newPassword">New Password</label>
                <input type="password" id="newPassword" name="newPassword"
                       required autocomplete="new-password" minlength="8"
                       placeholder="Min 8 characters">
              </div>
              <div class="form-group">
                <label for="confirmPassword">Confirm New Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       required autocomplete="new-password"
                       placeholder="Repeat new password">
              </div>

              <!-- Password strength hint -->
              <p style="font-size:12px;color:#6c757d;margin-bottom:16px">
                Use at least 8 characters with a mix of letters, numbers and symbols.
              </p>

              <button type="submit" class="btn btn-primary" style="width:100%">
                Update Password
              </button>
            </form>
          </div>
        </div>

      </div><!-- /grid -->

    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
