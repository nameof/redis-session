package cas.test.service;

import java.lang.reflect.Method;

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
	
	@Test
	public void testProxyObject(){
		System.out.println(userService);
		Method[] methods = userService.getClass().getMethods();
		for(Method m : methods){
			System.out.println(m.getName());
		}
	}
}
