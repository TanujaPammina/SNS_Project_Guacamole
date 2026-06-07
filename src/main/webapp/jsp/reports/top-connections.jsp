<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--
Connection Analytics Dashboard Enhancement
Contributor: Venkatesh
Features:

* Analytics summary cards
* Search filter
* Improved table styling
* Enhanced empty state
  --%>

<!DOCTYPE html>

<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Top Connections Analytics — Guacamole Admin</title>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/styles.css">

<style>

.analytics-banner{
    display:flex;
    justify-content:space-between;
    align-items:center;
    flex-wrap:wrap;
    margin-bottom:20px;
    padding:18px;
    border-radius:10px;
    background:#f8fbff;
    border:1px solid #e3ebf6;
}

.analytics-banner h2{
    margin:0;
}

.analytics-banner p{
    margin-top:5px;
    color:#666;
}

.stats-grid{
    display:grid;
    grid-template-columns:repeat(auto-fit,minmax(180px,1fr));
    gap:15px;
    margin-bottom:20px;
}

.stat-card{
    background:#fff;
    border:1px solid #e3ebf6;
    border-radius:10px;
    padding:15px;
    text-align:center;
}

.stat-card h4{
    margin:0;
    color:#666;
    font-size:13px;
}

.stat-card span{
    font-size:24px;
    font-weight:700;
}

.search-box{
    padding:8px 12px;
    border:1px solid #d9e2ef;
    border-radius:6px;
    min-width:220px;
}

.analytics-table{
    width:100%;
}

.analytics-table tbody tr{
    transition:all .2s ease;
}

.analytics-table tbody tr:hover{
    background:#f7fbff;
}

.rank-badge{
    background:#eef4ff;
    border-radius:20px;
    padding:4px 10px;
    font-weight:700;
}

.session-badge{
    background:#f3f4f6;
    border-radius:14px;
    padding:4px 10px;
    font-weight:600;
}

.empty-state{
    text-align:center;
    padding:30px;
}

</style>

</head>

<body>

<div class="layout">

```
<jsp:include page="/jsp/layout/sidebar.jsp"/>

<div class="main">

    <jsp:include page="/jsp/layout/topbar.jsp">
        <jsp:param name="title" value="Top Connections"/>
    </jsp:include>

    <div class="content">

        <div class="analytics-banner">
            <div>
                <h2>🔗 Connection Analytics Dashboard</h2>
                <p>Monitor connection usage and session performance.</p>
            </div>

            <div>
                <strong>Top Connections Report</strong>
            </div>
        </div>

        <div class="stats-grid">

            <div class="stat-card">
                <h4>Report Type</h4>
                <span>20</span>
            </div>

            <div class="stat-card">
                <h4>Analytics</h4>
                <span>✓</span>
            </div>

            <div class="stat-card">
                <h4>Status</h4>
                <span>Live</span>
            </div>

        </div>

        <div class="card">

            <div class="card-header">

                <h2>Top 20 Connections by Usage</h2>

                <input id="table-search"
                       class="search-box"
                       type="search"
                       placeholder="Search connections...">

            </div>

            <div class="table-wrap">

                <table class="analytics-table" id="connectionsTable">

                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Connection Name</th>
                            <th>Total Sessions</th>
                            <th>Total Duration</th>
                            <th>Avg Duration</th>
                        </tr>
                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${empty connStats}">
                            <tr>
                                <td colspan="5">
                                    <div class="empty-state">
                                        <h3>No Connection Data Available</h3>
                                        <p>Connection analytics data is currently unavailable.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>

                        <c:otherwise>

                            <c:forEach var="c"
                                       items="${connStats}"
                                       varStatus="s">

                                <tr>

                                    <td>
                                        <span class="rank-badge">
                                            #${s.index + 1}
                                        </span>
                                    </td>

                                    <td>
                                        <strong>${c.connectionName}</strong>
                                    </td>

                                    <td>
                                        <span class="session-badge">
                                            ${c.totalSessions}
                                        </span>
                                    </td>

                                    <td>
                                        ${c.totalDurationFormatted}
                                    </td>

                                    <td>
                                        ${c.avgDurationFormatted}
                                    </td>

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
```

</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>

<script>

document.addEventListener("DOMContentLoaded", function(){

    const search =
        document.getElementById("table-search");

    if(!search) return;

    search.addEventListener("keyup", function(){

        const filter =
            this.value.toLowerCase();

        document
            .querySelectorAll("#connectionsTable tbody tr")
            .forEach(function(row){

                const text =
                    row.textContent.toLowerCase();

                row.style.display =
                    text.includes(filter)
                    ? ""
                    : "none";
            });

    });

});

</script>

</body>
</html>

