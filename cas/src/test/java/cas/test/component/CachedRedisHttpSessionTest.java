package cas.test.component;

import java.util.Arrays;

import org.junit.Test;

import cas.custom.component.CachedRedisHttpSession;

public class CachedRedisHttpSessionTest {

	@Test
	public void testGetAndSetVal(){
		CachedRedisHttpSession session = new CachedRedisHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		session.setMaxInactiveInterval(200);
		System.out.println(session.getAttribute("userInfo").toString());
		session.commit();
		
		CachedRedisHttpSession session2 = new CachedRedisHttpSession(null, "token");
		Person get = (Person) session2.getAttribute("userInfo");
		System.out.println(get.getName());
	}
	
	@Test
	public void testGetValueNames(){
		CachedRedisHttpSession session = new CachedRedisHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		session.commit();
		
		CachedRedisHttpSession session2 = new CachedRedisHttpSession(null, "token");
		System.out.println(Arrays.asList(session2.getValueNames()));
	}
}
