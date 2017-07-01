package cas.custom.component;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import cas.cdao.CacheDao;
import cas.cdao.CacheDaoFactory;

/**
 * {@link cas.custom.component.BufferedCacheHttpSession}实例会在构造时，尝试从缓存中加载所有的用户会话数据
 * （包括maxInactiveInterval）缓存到本地的ConcurrentHashMap中.
 * 
 * 在当前会话期间，每一次对Session中Attribute的操作都是对于{@link cas.custom.component.BufferedCacheHttpSession}
 * 对象缓存的attributes操作.
 * 
 * 当前请求完成之后，所有attributes通过{@link cas.filter.CacheSessionFilter}调用
 * {@link cas.custom.component.BufferedCacheHttpSession}的commit方法提交到缓存中，同时设置expire过期时间.
 * 
 * @author ChengPan
 */
public class BufferedCacheHttpSession extends HttpSessionWrapper 
			implements CustomSessionProcessor {
	
	private static final long serialVersionUID = -248646772305855733L;

	/** 默认过期时间为30分钟 */
    private static final int DEFAULT_EXPIRE = 60 * 30;
    
    /** 用于存储maxInactiveInterval到缓存的key */
    private static final String CACHE_EXPIRE_KEY = "maxInactiveInterval";
    
    private Map<String,Object> attributes = new ConcurrentHashMap<>();
    
    private final String token;
    
    private int maxInactiveInterval = DEFAULT_EXPIRE;
    
    /** Session是否永不过期 */
    private boolean isPersistKey = false;
    
    private CacheDao cacheDao = CacheDaoFactory.newCacheDaoInstance();
    
    // TODO 处理isInvalid状态,针对invalid会话抛出IllegalStateException异常
    private boolean isInvalid = false;
    
	public BufferedCacheHttpSession(HttpSession session, String token) {
		super(session);
		this.token = token;
		initialize();
	}

	@Override
	public void initialize() {
		//获取缓存中所有"Attribute"，缓存到本地
		attributes.putAll(cacheDao.getAllAttribute(token));
		
		//初始化maxInactiveInterval
		Integer expire = (Integer) attributes.get(CACHE_EXPIRE_KEY);
		if (expire != null) {
			if (expire == -1) {
				isPersistKey = true;
			}
			this.maxInactiveInterval = expire;
		}
	}

	@Override
	public void commit() {
		//提交Session属性到缓存中
		cacheDao.setAllAttributes(token, attributes);
		setExpireToCache();
	}

	@Override  
    public int getMaxInactiveInterval() { 
        return maxInactiveInterval;
    }
	
	@Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.isPersistKey = false;
		this.maxInactiveInterval = maxInactiveInterval;
    	attributes.put(CACHE_EXPIRE_KEY, maxInactiveInterval);
    }
	
    @Override  
    public String getId() {  
        return token;  
    }
    
  	@Override  
    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributes.keySet()).elements();  
    }

    @Override  
	public String[] getValueNames() {
    	Set<String> keys = attributes.keySet();
		return keys.toArray(new String[keys.size()]);
	} 
	
	@Override  
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override  
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	@Override  
	public void invalidate() {
		this.isInvalid = true;
		attributes.clear();
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
