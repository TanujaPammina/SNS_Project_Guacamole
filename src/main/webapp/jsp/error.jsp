<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Error — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp"><jsp:param name="title" value="Error"/></jsp:include>
    <div class="content">
      <div class="alert alert-danger">
        <strong>An error occurred:</strong>
        ${not empty errorMessage ? errorMessage : 'An unexpected error occurred. Please try again.'}
      </div>
      <a class="btn btn-outline" href="${pageContext.request.contextPath}/dashboard">← Back to Dashboard</a>
    </div>
  </div>
</div>
</body>
</html>
