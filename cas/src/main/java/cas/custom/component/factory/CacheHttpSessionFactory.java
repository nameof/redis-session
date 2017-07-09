package cas.custom.component.factory;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpSession;

import cas.support.ConfigLoader;

/**
 * 自定义Session实例工厂
 * @author ChengPan
 */
public class CacheHttpSessionFactory {

	private CacheHttpSessionFactory() {}

	private static Class<?> clazz;

	private static final String SESSION_IMPL_CLASS_KEY = "cache.httpsession.impl.class";
	
	static {
		try {
			clazz = Class
					.forName(ConfigLoader.getConfig(SESSION_IMPL_CLASS_KEY));
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
