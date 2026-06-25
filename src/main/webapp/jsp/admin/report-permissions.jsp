<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Report Permissions — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
  <style>
    .role-tabs          { display:flex; gap:8px; margin-bottom:24px; flex-wrap:wrap; }
    .role-tab           { padding:8px 22px; border-radius:6px; border:2px solid #e3ebf6;
                          background:#fff; color:#1e2a38; font-size:14px; font-weight:600;
                          cursor:pointer; text-decoration:none; transition:all .15s; }
    .role-tab:hover     { border-color:#2c7be5; color:#2c7be5; }
    .role-tab.active    { background:#2c7be5; border-color:#2c7be5; color:#fff; }
    .perm-table         { width:100%; border-collapse:collapse; }
    .perm-table th      { text-align:left; padding:10px 14px; background:#f5f7fb;
                          font-size:12px; font-weight:700; color:#6b7a99; border-bottom:1px solid #e3ebf6; }
    .perm-table td      { padding:12px 14px; border-bottom:1px solid #f0f3f9; font-size:14px; }
    .perm-table tr:last-child td { border-bottom:none; }
    .perm-table tr:hover td { background:#fafcff; }
    .check-cell         { text-align:center; }
    .check-cell input   { width:18px; height:18px; cursor:pointer; accent-color:#2c7be5; }
    .superadmin-notice  { background:#edf5ff; border:1px solid #b8d4f8; border-radius:8px;
                          padding:16px 20px; font-size:14px; color:#1a4a8a; margin-bottom:24px; }
    .save-row           { display:flex; align-items:center; gap:12px; margin-top:20px; }
    .select-all-row     { display:flex; gap:8px; margin-bottom:12px; }
    .btn-sm             { padding:5px 12px; font-size:13px; border-radius:5px; cursor:pointer;
                          border:1px solid #d0d9e8; background:#f5f7fb; color:#1e2a38;
                          font-weight:500; transition:background .15s; }
    .btn-sm:hover       { background:#e3ebf6; }
  </style>
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <jsp:include page="/jsp/layout/topbar.jsp">
      <jsp:param name="title" value="Report Permissions"/>
    </jsp:include>
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

      <%-- Page intro --%>
      <p style="color:#6b7a99;font-size:14px;margin-bottom:20px">
        Select a role below to configure which of the 10 reports that role can access.
        <strong>Super Admin</strong> always has full access and cannot be restricted.
      </p>

      <%-- Role tabs --%>
      <div class="role-tabs">
        <c:forEach var="r" items="${allRoles}">
          <a href="${pageContext.request.contextPath}/admin/report-permissions?role=${r.name()}"
             class="role-tab ${selectedRole.name() == r.name() ? 'active' : ''}">
            ${r.displayName}
          </a>
        </c:forEach>
      </div>

      <%-- Super Admin notice (shown when Super Admin role would be selected — not possible via tabs,
           but shown as info regardless) --%>
      <div class="superadmin-notice">
        <strong>Super Admin</strong> always has access to <em>all</em> reports.
        Use the tabs above to configure <strong>IT Admin</strong> and <strong>Auditor</strong> access.
      </div>

      <%-- Permission checkboxes --%>
      <div class="card">
        <div class="card-header">
          <h2>
            Reports visible to: <strong>${selectedRole.displayName}</strong>
          </h2>
        </div>
        <div class="card-body" style="padding:20px">

          <form method="post"
                action="${pageContext.request.contextPath}/admin/report-permissions?role=${selectedRole.name()}">

            <%-- Select all / deselect all helpers --%>
            <div class="select-all-row">
              <button type="button" class="btn-sm" onclick="setAll(true)">Select All</button>
              <button type="button" class="btn-sm" onclick="setAll(false)">Deselect All</button>
            </div>

            <table class="perm-table">
              <thead>
                <tr>
                  <th>Report</th>
                  <th class="check-cell" style="width:100px">Allow Access</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="key" items="${reportKeys}">
                  <tr>
                    <td>
                      <c:out value="${reportLabels[key]}"/>
                      <span style="margin-left:6px;font-size:11px;color:#aab4c8;font-family:monospace">
                        (${key})
                      </span>
                    </td>
                    <td class="check-cell">
                      <input type="checkbox"
                             name="reports"
                             value="${key}"
                             class="report-check"
                             ${permissions[key] ? 'checked' : ''}
                             aria-label="Allow ${key}">
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>

            <div class="save-row">
              <button type="submit" class="btn btn-primary">Save Permissions</button>
              <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline">
                Back to Admin Users
              </a>
            </div>

          </form>

        </div>
      </div>

    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
  function setAll(checked) {
    document.querySelectorAll('.report-check').forEach(function(cb) {
      cb.checked = checked;
    });
  }
</script>
</body>
</html>
