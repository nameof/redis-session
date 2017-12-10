<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>欢迎</title>
  </head>
  <body>
  		welcome !  ${user.name}<br>
  		<a href="${pageContext.request.contextPath}/logout">注销登录</a>
  </body>
</html>
