package cas.custom.component.session;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import cas.cdao.CacheDao;
import cas.cdao.factory.CacheDaoFactory;

/**
 * {@link cas.custom.component.session.BufferedCacheHttpSession}实例会在构造时
 * 尝试从缓存中加载所有的用户会话数据（包括所有属性和maxInactiveInterval） * 缓存到本地的
 * {@link cas.custom.component.session.BufferedCacheHttpSession#attributes}中.<br>
 * 
 * 在当前会话期间，每一次对Session中Attribute的操作都是对于{@link cas.custom.component.session.BufferedCacheHttpSession}
 * 对象ConcurrentHashMap属性缓存的attributes操作.<br>
 * 
 * 当前请求完成之后，所有attributes通过{@link cas.filter.CacheSessionFilter}调用
 * {@link cas.custom.component.session.BufferedCacheHttpSession}的commit方法提交到缓存中，同时设置expire过期时间.
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
    
    /** 本地属性集合 */
    private Map<String,Object> attributes = new ConcurrentHashMap<>();
    
    /** session id */
    private final String token;
    
    private int maxInactiveInterval = DEFAULT_EXPIRE;
    
    /** Session是否永不过期 */
    private boolean isPersistKey = false;
    
    private static CacheDao cacheDao = CacheDaoFactory.newCacheDaoInstance();
    
	public BufferedCacheHttpSession(HttpSession session, String token) {
		super(session);
		this.token = token;
		initialize();
	}

	/**
	 * 初始化session属性信息
	 */
	@Override
	public void initialize() {
		
		//获取缓存中所有"Attribute"，缓存到本地
		//attributes.putAll(cacheDao.getAllAttribute(token));
		@SuppressWarnings("unchecked")
		Map<String,Object> all = (Map<String, Object>) cacheDao.get(token);
		if (all != null) {
			attributes.putAll(all);
		}
		
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
//		//提交Session属性到缓存中
//		cacheDao.setAllAttributes(token, attributes);
//		
//		//设置expire
//		setExpireToCache();
		cacheDao.setWithExpire(token, attributes, maxInactiveInterval);
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
  		checkValid();
        return new Vector<String>(attributes.keySet()).elements();  
    }

    @Override  
	public String[] getValueNames() {
    	checkValid();
    	Set<String> keys = attributes.keySet();
		return keys.toArray(new String[keys.size()]);
	} 
	
	@Override  
	public void setAttribute(String name, Object value) {
		checkValid();
		attributes.put(name, value);
	}

	@Override  
	public Object getAttribute(String name) {
		checkValid();
		return attributes.get(name);
	}
	
	@Override  
    public void removeAttribute(String name) {  
		checkValid();
		attributes.remove(name);
	}
	
	@Override  
	public void invalidate() {
		checkValid();
		super.invalidate();//invalidate原始HttpSession
		this.isInvalid = true;
		attributes.clear();
		cacheDao.del(token);
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
