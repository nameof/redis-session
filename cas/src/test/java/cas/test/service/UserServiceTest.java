package cas.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cas.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration({"classpath:spring-mybatis.xml"})  
public class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Test
	public void testGetUser(){
		System.out.println(userService.getUserByNamePasswd("123",""));
	}
}
