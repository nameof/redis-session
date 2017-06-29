package cas.custom.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpSession;

/**
 * 自定义Session实例工厂
 * @author ChengPan
 */
public class CustomeHttpSessionFactory {

	private CustomeHttpSessionFactory() {
	}

	private static Class<?> clazz;

	static {
		try {
			clazz = Class
					.forName("cas.custom.component.DefaultRedisHttpSession");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("can not found session class", e);
		}
	}

	public static HttpSession newSessionInstance(HttpSession session, String token) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(HttpSession.class, String.class);
			return (HttpSession) constructor.newInstance(session, token);
		} catch (InstantiationException e) {
			throw new RuntimeException("InstantiationException in HttpSessionFactory", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalAccessException in HttpSessionFactory", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException in HttpSessionFactory", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("InvocationTargetException in HttpSessionFactory", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("NoSuchMethodException in HttpSessionFactory", e);
		} catch (SecurityException e) {
			throw new RuntimeException("SecurityException in HttpSessionFactory", e);
		}
	}
}
