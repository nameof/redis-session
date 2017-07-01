package cas.cdao;

public class CacheDaoFactory {
	private CacheDaoFactory() {}

	private static Class<?> clazz;

	static {
		try {
			clazz = Class
					.forName("cas.cdao.impl.RedisCacheDao");
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("can not found class", e);
		}
	}

	public static CacheDao newCacheDaoInstance() {
		try {
			return (CacheDao) clazz.newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
