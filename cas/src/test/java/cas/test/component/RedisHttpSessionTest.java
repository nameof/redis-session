package cas.test.component;

import org.junit.Test;

import cas.custom.component.RedisHttpSession;

public class RedisHttpSessionTest {

	
	@Test
	public void testOperatAttribute(){
		RedisHttpSession session = new RedisHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		System.out.println(session.getAttribute("userInfo").toString());
	}
}
