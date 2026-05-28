# Run from D:\GuacamoleAdminProject:  .\scaffold.ps1
$root = "D:\GuacamoleAdminProject"

# create directories
$dirs = @(
  "$root\src\main\java\com\guacamole\controller",
  "$root\src\main\java\com\guacamole\service",
  "$root\src\main\java\com\guacamole\dao",
  "$root\src\main\java\com\guacamole\model",
  "$root\src\main\java\com\guacamole\util",
  "$root\src\main\webapp\jsp",
  "$root\src\main\webapp\css",
  "$root\src\main\webapp\js"
)
foreach ($d in $dirs) { New-Item -Path $d -ItemType Directory -Force | Out-Null }

# create Java files
@"
package com.guacamole.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(\"<html><body><h1>Guacamole Admin</h1><p>Hello from HelloServlet</p></body></html>\");
    }
}
"@ | Set-Content -Path "$root\src\main\java\com\guacamole\controller\HelloServlet.java" -Encoding UTF8

@"
package com.guacamole.service;

public class UserService {
    // TODO: implement service methods
}
"@ | Set-Content -Path "$root\src\main\java\com\guacamole\service\UserService.java" -Encoding UTF8

@"
package com.guacamole.dao;

public class UserDao {
    // TODO: implement DAO methods
}
"@ | Set-Content -Path "$root\src\main\java\com\guacamole\dao\UserDao.java" -Encoding UTF8

@"
package com.guacamole.model;

public class User {
    private Long id;
    private String username;
    private String passwordHash;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String p) { this.passwordHash = p; }
}
"@ | Set-Content -Path "$root\src\main\java\com\guacamole\model\User.java" -Encoding UTF8

@"
package com.guacamole.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    public static Connection getConnection() throws SQLException {
        // TODO: read from config
        String url = \"jdbc:mysql://localhost:3306/yourdb\";
        String user = \"root\";
        String pass = \"password\";
        return DriverManager.getConnection(url, user, pass);
    }
}
"@ | Set-Content -Path "$root\src\main\java\com\guacamole\util\DbUtil.java" -Encoding UTF8

# create webapp files
@"
<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>
<!DOCTYPE html>
<html>
<head>
  <title>Guacamole Admin</title>
  <link rel=\"stylesheet\" href=\"../css/styles.css\">
</head>
<body>
  <h1>Guacamole Admin</h1>
  <p>Welcome — JSP index.</p>
  <script src=\"../js/app.js\"></script>
</body>
</html>
"@ | Set-Content -Path "$root\src\main\webapp\jsp\index.jsp" -Encoding UTF8

@"
body { font-family: Arial, Helvetica, sans-serif; margin: 20px; }
"@ | Set-Content -Path "$root\src\main\webapp\css\styles.css" -Encoding UTF8

@"
console.log('Guacamole Admin JS loaded');
"@ | Set-Content -Path "$root\src\main\webapp\js\app.js" -Encoding UTF8

Write-Output 'Scaffold complete.'