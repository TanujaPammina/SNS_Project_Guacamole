<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${empty editUser ? 'New Admin User' : 'Edit Admin User'} — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">
        ${empty editUser ? 'New Admin User' : 'Edit Admin User'}
      </span>
      <span class="topbar-user">${sessionScope.currentUser.username}</span>
    </div>
    <div class="content">

      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
      </c:if>

      <div class="card" style="max-width:560px">
        <div class="card-header">
          <h2>${empty editUser ? 'Create New Admin User' : 'Edit: '.concat(editUser.username)}</h2>
        </div>
        <div class="card-body">

          <form method="post"
                action="${pageContext.request.contextPath}/admin/users?action=${empty editUser ? 'create' : 'update'}&id=${editUser.id}">

            <%-- Username (read-only on edit) --%>
            <div class="form-group">
              <label for="username">Username *</label>
              <input type="text" id="username" name="username"
                     value="${not empty editUser ? editUser.username : dto.username}"
                     ${not empty editUser ? 'readonly style="background:#f5f7fb"' : 'required'}
                     placeholder="e.g. john.doe" maxlength="64">
            </div>

            <%-- Full Name --%>
            <div class="form-group">
              <label for="fullName">Full Name</label>
              <input type="text" id="fullName" name="fullName"
                     value="${not empty editUser ? editUser.fullName : dto.fullName}"
                     placeholder="e.g. John Doe" maxlength="128">
            </div>

            <%-- Email --%>
            <div class="form-group">
              <label for="email">Email</label>
              <input type="email" id="email" name="email"
                     value="${not empty editUser ? editUser.email : dto.email}"
                     placeholder="e.g. john@example.com" maxlength="255">
            </div>

            <%-- Role --%>
            <div class="form-group">
              <label for="role">Role *</label>
              <select id="role" name="role" required
                      style="width:100%;padding:10px 12px;border:1px solid #e3ebf6;border-radius:5px;font-size:14px">
                <option value="">— Select Role —</option>
                <c:forEach var="r" items="${roles}">
                  <option value="${r.name()}"
                    ${(not empty editUser && editUser.role.name() == r.name()) ||
                      (not empty dto && dto.role == r.name()) ? 'selected' : ''}>
                    ${r.displayName} — ${r.description}
                  </option>
                </c:forEach>
              </select>
            </div>

            <%-- Password --%>
            <div class="form-group">
              <label for="password">
                Password ${not empty editUser ? '(leave blank to keep current)' : '*'}
              </label>
              <input type="password" id="password" name="password"
                     ${empty editUser ? 'required' : ''}
                     placeholder="Min 8 characters" minlength="8" autocomplete="new-password">
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirm Password</label>
              <input type="password" id="confirmPassword" name="confirmPassword"
                     placeholder="Repeat password" autocomplete="new-password">
            </div>

            <%-- Active toggle --%>
            <div class="form-group" style="display:flex;align-items:center;gap:10px">
              <input type="checkbox" id="active" name="active"
                     ${(empty editUser || editUser.active) ? 'checked' : ''}
                     style="width:16px;height:16px">
              <label for="active" style="margin:0;font-size:14px;font-weight:normal;color:#1e2a38">
                Account is active
              </label>
            </div>

            <div style="display:flex;gap:10px;margin-top:24px">
              <button type="submit" class="btn btn-primary">
                ${empty editUser ? 'Create User' : 'Save Changes'}
              </button>
              <a class="btn btn-outline"
                 href="${pageContext.request.contextPath}/admin/users">Cancel</a>
            </div>

          </form>
        </div>
      </div>

    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
