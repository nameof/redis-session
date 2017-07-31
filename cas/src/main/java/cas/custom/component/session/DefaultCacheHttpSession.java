package cas.custom.component.session;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import cas.cdao.CacheDao;
import cas.cdao.factory.CacheDaoFactory;
/**
 * 默认情况下，{@link cas.custom.component.session.DefaultCacheHttpSession}实例会在构造时尝试从缓存中
 * 加载maxInactiveInterval信息（如果有的话）.
 * 
 * 每一次对Session中Attribute都会直接导致{@link cas.custom.component.session.DefaultCacheHttpSession}
 * 与缓存进行直接交互.
 * 
 * 当前请求完成之后，通过{@link cas.filter.CacheSessionFilter}调用{@link cas.custom.component.session.DefaultCacheHttpSession}
 * 的commit方法将expire（maxInactiveInterval）提交到缓存中.
 * 
 * @author ChengPan
 */
public class DefaultCacheHttpSession extends HttpSessionWrapper implements
		CustomSessionProcessor {

	private static final long serialVersionUID = 3977740308601865675L;

	/** 默认过期时间为30分钟  */
	private static final int DEFAULT_EXPIRE = 60 * 30;

	/**
	 * 一旦调用setMaxInactiveInterval，就会把maxInactiveInterval值存入缓存，
	 * 以CACHE_EXPIRE_KEY为token的fieldKey进行存储
	 */
	private static final String CACHE_EXPIRE_KEY = "maxInactiveInterval";

	private final String token;

	private int maxInactiveInterval = DEFAULT_EXPIRE;

	private boolean isPersistKey = false;
	
    // TODO 处理isInvalid状态,针对invalid会话抛出IllegalStateException异常
	private boolean isInvalid = false;
	
	private CacheDao cacheDao = CacheDaoFactory.newCacheDaoInstance();

	public DefaultCacheHttpSession(HttpSession session, String token) {
		super(session);
		this.token = token;
		initialize();
	}

	@Override
	public void initialize() {
		// 从缓存中读取maxInactiveInterval信息
		Integer originalExpire = (Integer) cacheDao.getAttribute(token, CACHE_EXPIRE_KEY);
		if (originalExpire != null) {
			if (originalExpire == -1) {
				isPersistKey = true;
			}
			this.maxInactiveInterval = originalExpire;
		}
	}

	@Override
	public void commit() {
		setExpireToCache();
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
		cacheDao.setAttribute(token, CACHE_EXPIRE_KEY, maxInactiveInterval);
	}

	@Override
	public String getId() {
		return token;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return cacheDao.getAttributeNames(token);
	}

	@Override
	public String[] getValueNames() {
		return cacheDao.getValueNames(token);
	}

	@Override
	public void setAttribute(String name, Object value) {
		cacheDao.setAttribute(token, name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return cacheDao.getAttribute(token, name);
	}

	@Override
	public void invalidate() {
		this.isInvalid = true;
		cacheDao.del(token);
	}
	
	public boolean isInvalid() {
		return this.isInvalid;
	}
	
	private void setExpireToCache() {
		if (maxInactiveInterval == -1) {
			if (!isPersistKey) {
				cacheDao.setPersist(token);
				isPersistKey = true;
			}
		}
		else {
			cacheDao.setExpire(token, maxInactiveInterval);
		}
	}
}