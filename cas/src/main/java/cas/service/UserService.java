package cas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cas.dao.UserDao;
import cas.models.User;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;
	
	public User getUserByNamePasswd(String username,String passwd){
		return userDao.getUserByName(username);
	}
}
