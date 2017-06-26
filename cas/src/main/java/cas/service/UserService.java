package cas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cas.dao.UserDao;
import cas.models.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	
	public User verifyUserLogin(User inputUser) {
		User user = userDao.getUserByName(inputUser.getName());
		if (user == null || !user.getPasswd().equals(inputUser.getPasswd())) {
			return null;
		}
		user.setPasswd(null);
		return user;
	}
}
