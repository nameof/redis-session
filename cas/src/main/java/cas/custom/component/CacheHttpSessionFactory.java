package cas.custom.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpSession;

/**
 * 自定义Session实例工厂
 * @author ChengPan
 */
public class CacheHttpSessionFactory {

	private CacheHttpSessionFactory() {}

	private static Class<?> clazz;

	static {
		try {
			clazz = Class
					.forName("cas.custom.component.DefaultCacheHttpSession");
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("can not found session class", e);
		}
	}

	public static HttpSession newSessionInstance(HttpSession session, String token) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(HttpSession.class, String.class);
			return (HttpSession) constructor.newInstance(session, token);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
