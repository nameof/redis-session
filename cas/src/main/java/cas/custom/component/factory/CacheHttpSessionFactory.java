package cas.custom.component.factory;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpSession;

import cas.custom.component.session.HttpSessionWrapper;
import cas.support.ConfigLoader;

/**
 * 根据{@link cas.support.ConfigLoader}获取自定义HttpSession实现类配置，实例化自定义HttpSession
 * @author ChengPan
 */
public class CacheHttpSessionFactory {

	private CacheHttpSessionFactory() {}

	private static final Class<?> clazz;

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

	public static HttpSessionWrapper newSessionInstance(HttpSession session, String token) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(HttpSession.class, String.class);
			return (HttpSessionWrapper) constructor.newInstance(session, token);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
