package sso.test;

import org.junit.Test;

import sso.custom.component.Person;
import sso.custom.component.RedisHttpSession;

public class RedisHttpSessionTest {

	
	@Test
	public void testOperatAttribute(){
		RedisHttpSession session = new RedisHttpSession(null, "token");
		Person p = new Person("Ёлей",123);
		session.setAttribute("userInfo", p);
		
		System.out.println(session.getAttribute("userInfo").toString());
	}
}
