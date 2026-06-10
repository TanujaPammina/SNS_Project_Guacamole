<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reset Password — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="login-page">
  <div class="login-box">
    <h1>Reset Password</h1>
    <p class="sub">Setting new password for: <strong>${username}</strong></p>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/reset-password">
      <input type="hidden" name="token" value="${token}">

      <div class="form-group">
        <label for="newPassword">New Password</label>
        <div style="position:relative">
          <input type="password" id="newPassword" name="newPassword"
                 required minlength="8" autofocus autocomplete="new-password"
                 placeholder="Min 8 characters" style="padding-right:48px">
          <button type="button" id="toggle1"
                  style="position:absolute;right:12px;top:50%;transform:translateY(-50%);
                         background:none;border:none;cursor:pointer;font-size:18px;
                         color:#6c757d;line-height:1;padding:0">👁</button>
        </div>
      </div>

      <div class="form-group">
        <label for="confirmPassword">Confirm New Password</label>
        <div style="position:relative">
          <input type="password" id="confirmPassword" name="confirmPassword"
                 required autocomplete="new-password"
                 placeholder="Repeat new password" style="padding-right:48px">
          <button type="button" id="toggle2"
                  style="position:absolute;right:12px;top:50%;transform:translateY(-50%);
                         background:none;border:none;cursor:pointer;font-size:18px;
                         color:#6c757d;line-height:1;padding:0">👁</button>
        </div>
      </div>

      <p style="font-size:12px;color:#6c757d;margin-bottom:16px">
        Use at least 8 characters with letters, numbers and symbols.
      </p>

      <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
    </form>

    <div style="text-align:center;margin-top:16px">
      <a href="${pageContext.request.contextPath}/login"
         style="font-size:13px;color:#6c757d">← Back to Login</a>
    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
  function setupToggle(btnId, inputId) {
    document.getElementById(btnId).addEventListener('click', function() {
      var input = document.getElementById(inputId);
      if (input.type === 'password') {
        input.type = 'text';
        this.textContent = '🙈';
      } else {
        input.type = 'password';
        this.textContent = '👁';
      }
    });
  }
  setupToggle('toggle1', 'newPassword');
  setupToggle('toggle2', 'confirmPassword');
</script>
</body>
</html>
