package cas.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cas.models.User;
import cas.service.UserService;

@Controller
public class SystemController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/processLogin",method=RequestMethod.POST)
	public String processLogin(String username, String passwd, HttpSession session
			,Model model){
		User user = userService.verifyUserLogin(username, passwd);
		if(user == null){
			model.addAttribute("error", "用户名或密码错误!");
			return "login";
		} else{
			session.setAttribute("user", user);
			return "redirect:/index";
		}
	}
}
