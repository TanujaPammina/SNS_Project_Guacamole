<%@ page contentType="text/html;charset=UTF-8" %>
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

    <% if (request.getAttribute("errorMessage") != null) { %>
      <div class="alert alert-danger">${errorMessage}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login" method="post">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username"
               autocomplete="username" required autofocus>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password"
               autocomplete="current-password" required>
      </div>
      <button type="submit" class="btn btn-primary btn-block">Sign In</button>
    </form>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
