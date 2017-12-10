<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  </head>
  
  <body>
   welcome to client! ${sessionScope.user.name}<br>
   <a href="${sessionScope.GLOBAL_LOGOUT_URL}">注销登录</a>
  </body>
</html>
