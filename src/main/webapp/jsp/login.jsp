<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Login — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="login-page">
  <div class="login-box">
    <h1>Guacamole Admin</h1>
    <p class="sub">Sign in to access the administration portal</p>

    <c:if test="${not empty sessionScope.flashSuccess}">
      <div class="alert alert-info" style="background:#d4f5e9;color:#00875a;border-color:#b7ebd8">
        ✓ ${sessionScope.flashSuccess}
      </div>
      <c:remove var="flashSuccess" scope="session"/>
    </c:if>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username"
               autocomplete="username" required autofocus>
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <div style="position:relative">
          <input type="password" id="password" name="password"
                 autocomplete="current-password" required
                 style="padding-right:70px">
          <button type="button"
                  onclick="var f=document.getElementById('password');if(f.type==='password'){f.type='text';this.textContent='HIDE';}else{f.type='password';this.textContent='SHOW';}"
                  style="position:absolute;right:10px;top:50%;transform:translateY(-50%);
                         background:#e3ebf6;border:1px solid #c0d0e8;border-radius:4px;
                         cursor:pointer;font-size:11px;font-weight:700;color:#2c7be5;
                         padding:3px 8px;line-height:1.4">
            SHOW
          </button>
        </div>
      </div>

      <div style="text-align:right;margin-top:-8px;margin-bottom:16px">
        <a href="${pageContext.request.contextPath}/forgot-password"
           style="font-size:12px;color:#6c757d">Forgot password?</a>
      </div>

      <button type="submit" class="btn btn-primary btn-block">Sign In</button>
    </form>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
