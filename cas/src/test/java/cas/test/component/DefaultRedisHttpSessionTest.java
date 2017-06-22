package cas.test.component;

import org.junit.Test;

import cas.custom.component.DefaultRedisHttpSession;

public class DefaultRedisHttpSessionTest {

	
	@Test
	public void testOperatAttribute(){
		DefaultRedisHttpSession session = new DefaultRedisHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		System.out.println(session.getAttribute("userInfo").toString());
	}
}
