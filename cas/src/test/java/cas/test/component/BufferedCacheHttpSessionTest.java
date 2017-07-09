package cas.test.component;

import java.util.Arrays;

import org.junit.Test;

import cas.custom.component.session.BufferedCacheHttpSession;

public class BufferedCacheHttpSessionTest {

	@Test
	public void testGetAndSetVal(){
		BufferedCacheHttpSession session = new BufferedCacheHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		session.setMaxInactiveInterval(200);
		System.out.println(session.getAttribute("userInfo").toString());
		session.commit();
		
		BufferedCacheHttpSession session2 = new BufferedCacheHttpSession(null, "token");
		Person get = (Person) session2.getAttribute("userInfo");
		System.out.println(get.getName());
	}
	
	@Test
	public void testGetValueNames(){
		BufferedCacheHttpSession session = new BufferedCacheHttpSession(null, "token");
		Person p = new Person("程攀",123);
		session.setAttribute("userInfo", p);
		session.commit();
		
		BufferedCacheHttpSession session2 = new BufferedCacheHttpSession(null, "token");
		System.out.println(Arrays.asList(session2.getValueNames()));
	}
}
