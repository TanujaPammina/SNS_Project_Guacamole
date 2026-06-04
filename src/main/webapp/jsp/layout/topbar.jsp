<%-- Reusable topbar fragment.
     Usage: <jsp:include page="/jsp/layout/topbar.jsp">
               <jsp:param name="title" value="Page Title"/>
            </jsp:include>
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<div class="topbar">
  <div class="topbar-left">
    <button class="menu-toggle" id="menu-toggle" aria-label="Open menu">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="6"  x2="21" y2="6"/>
        <line x1="3" y1="12" x2="21" y2="12"/>
        <line x1="3" y1="18" x2="21" y2="18"/>
      </svg>
    </button>
    <span class="topbar-title">${param.title}</span>
  </div>
  <span class="topbar-user">
    <a href="${pageContext.request.contextPath}/profile"
       style="display:flex;align-items:center;gap:6px;color:#6c757d;text-decoration:none">
      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
        <circle cx="12" cy="7" r="4"/>
      </svg>
      ${sessionScope.currentUser.username}
      <span class="badge badge-info" style="font-size:10px">${sessionScope.currentUser.roleDisplayName}</span>
    </a>
  </span>
</div>
