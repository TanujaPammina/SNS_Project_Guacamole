<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Forgot Password — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="login-page">
  <div class="login-box">

    <h1>Forgot Password?</h1>
    <p class="sub">Enter your email address and we'll send you a reset link.</p>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-info" style="background:#d4f5e9;color:#00875a;border-color:#b7ebd8">
        ✓ ${successMessage}
      </div>
      <div style="text-align:center;margin-top:16px">
        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline" style="width:100%">
          ← Back to Login
        </a>
      </div>
    </c:if>

    <c:if test="${empty successMessage}">
      <form method="post" action="${pageContext.request.contextPath}/forgot-password">
        <div class="form-group">
          <label for="email">Email Address</label>
          <input type="email" id="email" name="email"
                 required autofocus autocomplete="email"
                 placeholder="Enter your registered email">
        </div>
        <button type="submit" class="btn btn-primary btn-block">
          Send Reset Link
        </button>
      </form>

      <div style="text-align:center;margin-top:16px">
        <a href="${pageContext.request.contextPath}/login"
           style="font-size:13px;color:#6c757d">← Back to Login</a>
      </div>
    </c:if>

  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
