package cas.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cas.custom.component.CasHttpServletRequest;
import cas.models.User;
import cas.service.UserService;
import cas.utils.CookieUtil;

@Controller
public class SystemController {

	/**
	 * "记住我"过期策略为15天，作用于Cookie的maxAge，Session的MaxInactiveInterval
	 */
	private static final int REMEMBER_LOGIN_STATE_TIME = 15 * 24 * 60 * 60;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/processLogin",method=RequestMethod.POST)
	public String processLogin(String username, String passwd, Boolean rememberMe, 
			HttpSession session, HttpServletResponse response, HttpServletRequest request
			, Model model){
		User user = userService.verifyUserLogin(username, passwd);
		if(user == null){
			model.addAttribute("error", "用户名或密码错误!");
			return "login";
		} else{
			session.setAttribute("user", user);
			if (rememberMe == Boolean.TRUE) {
				session.setMaxInactiveInterval(REMEMBER_LOGIN_STATE_TIME);
				Cookie sessionCookie = CookieUtil.getCookie(request, CasHttpServletRequest.COOKIE_SESSION_KEY);
				if (sessionCookie != null) {
					sessionCookie.setMaxAge(REMEMBER_LOGIN_STATE_TIME);
					response.addCookie(sessionCookie);
				}
			}
			return "redirect:/index";
		}
	}
}
