<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Audit Log — Guacamole Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="layout">
  <jsp:include page="/jsp/layout/sidebar.jsp"/>
  <div class="main">
    <div class="topbar">
      <span class="topbar-title">Admin Audit Log</span>
      <span class="topbar-user">${sessionScope.loggedInUser}</span>
    </div>
    <div class="content">

      <!-- Filters -->
      <div class="card">
        <div class="card-header"><h2>Filters</h2></div>
        <div class="card-body">
          <form method="get" action="${pageContext.request.contextPath}/reports">
            <input type="hidden" name="type" value="audit-log">
            <div class="filter-bar">
              <label>Actor
                <input type="text" name="actor" value="${param.actor}" placeholder="All admins">
              </label>
              <label>Action
                <select name="action">
                  <option value="">All Actions</option>
                  <option value="LOGIN"          ${param.action == 'LOGIN'          ? 'selected' : ''}>LOGIN</option>
                  <option value="LOGIN_FAILED"   ${param.action == 'LOGIN_FAILED'   ? 'selected' : ''}>LOGIN_FAILED</option>
                  <option value="LOGOUT"         ${param.action == 'LOGOUT'         ? 'selected' : ''}>LOGOUT</option>
                  <option value="CREATE_USER"    ${param.action == 'CREATE_USER'    ? 'selected' : ''}>CREATE_USER</option>
                  <option value="EDIT_CONNECTION"${param.action == 'EDIT_CONNECTION'? 'selected' : ''}>EDIT_CONNECTION</option>
                </select>
              </label>
              <label>From
                <input type="date" id="from" name="from" value="${param.from}">
              </label>
              <label>To
                <input type="date" id="to" name="to" value="${param.to}">
              </label>
              <div style="display:flex;gap:6px;align-self:flex-end">
                <button type="button" class="btn btn-outline" data-days="7">Last 7d</button>
                <button type="button" class="btn btn-outline" data-days="30">Last 30d</button>
                <button type="submit" class="btn btn-primary">Apply</button>
              </div>
            </div>
          </form>
        </div>
      </div>

      <!-- Table -->
      <div class="card">
        <div class="card-header">
          <h2>Audit Events</h2>
          <input id="table-search" type="search" placeholder="Search…"
                 style="padding:6px 10px;border:1px solid #e3ebf6;border-radius:5px;font-size:13px">
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Time</th>
                <th>Actor</th>
                <th>Action</th>
                <th>Target</th>
                <th>Details</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty auditLogs}">
                  <tr><td colspan="6">
                    <div class="empty-state"><p>No audit events found.</p></div>
                  </td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="log" items="${auditLogs}">
                    <tr>
                      <td style="white-space:nowrap">${log.actionTime}</td>
                      <td>${log.actorUsername}</td>
                      <td>
                        <c:choose>
                          <c:when test="${log.action == 'LOGIN_FAILED'}">
                            <span class="badge badge-danger">${log.action}</span>
                          </c:when>
                          <c:when test="${log.action == 'LOGIN'}">
                            <span class="badge badge-success">${log.action}</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge badge-info">${log.action}</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${log.targetEntity}</td>
                      <td style="max-width:260px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap"
                          title="${log.details}">${log.details}</td>
                      <td><code>${log.remoteIp}</code></td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
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
