package cas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cas.dao.UserDao;
import cas.models.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	
	public User verifyUserLogin(String username,String passwd){
		User user = userDao.getUserByName(username);
		if(user == null || !user.getPasswd().equals(passwd)){
			return null;
		}
		return user;
	}
}
