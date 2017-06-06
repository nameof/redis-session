package cas.dao;

import org.springframework.stereotype.Repository;

import cas.models.User;

@Repository
public interface UserDao {

	User getUserByName(String userName);
}
