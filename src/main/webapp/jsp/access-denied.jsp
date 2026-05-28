<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Access Denied — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Access Denied</span>
      <span class="topbar-user">${sessionScope.currentUser.username}</span>
    </div>
    <div class="content">
      <div class="card" style="max-width:520px;margin:40px auto">
        <div class="card-body" style="text-align:center;padding:48px 32px">
          <svg width="56" height="56" viewBox="0 0 24 24" fill="none"
               stroke="#e63757" stroke-width="1.5" style="margin-bottom:16px">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          <h2 style="font-size:20px;margin-bottom:8px">Access Denied</h2>
          <p style="color:#6c757d;margin-bottom:24px">
            ${not empty errorMessage ? errorMessage :
              'You do not have permission to access this page.'}
          </p>
          <p style="color:#6c757d;font-size:13px;margin-bottom:24px">
            Your current role: <strong>${sessionScope.currentUser.roleDisplayName}</strong>
          </p>
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/dashboard">
            ← Back to Dashboard
          </a>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
