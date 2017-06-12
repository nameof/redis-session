<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>登录</title>
  </head>
  <body>
  		<form action="${pageContext.request.contextPath}/processLogin" method="post">
			用户名:<input type="text" name="username"/><br /><br />
			密码:<input type="password" name="passwd"/><br /><br />
			<input id="rememberMe" name="rememberMe" type="checkbox" checked="checked"/>
			<label for="rememberMe">记住我</label> 
			<br /><br />
			<input type="submit" value="登陆"/><span style="color:red;" >${error}</span>
		</form>
  </body>
</html>
