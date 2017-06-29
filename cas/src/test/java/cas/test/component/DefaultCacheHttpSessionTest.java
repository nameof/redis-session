package cas.test.component;

import org.junit.Test;

import cas.custom.component.DefaultCacheHttpSession;

public class DefaultCacheHttpSessionTest {

	
	@Test
	public void testOperatAttribute(){
		DefaultCacheHttpSession session = new DefaultCacheHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		System.out.println(session.getAttribute("userInfo").toString());
	}
}
