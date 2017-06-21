package cas.custom.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import cas.utils.RedisUtil;
//TODO 写注释
public class CachedRedisHttpSession extends HttpSessionWrapper 
			implements CustomSessionProcessor {
	/**
	 * 默认过期时间为30分钟
	 */
    private static final int DEFAULT_EXPIRE = 60 * 30;
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    /**
     * 用于存储maxInactiveInterval到Redis的key
     */
    private static final String REDIS_EXPIRE_KEY = "maxInactiveInterval";
    
    private Map<String,Object> attributes = new ConcurrentHashMap<>();
    
    private final String token;
    
    private final byte[] tokenBytes;
    
    private int maxInactiveInterval = DEFAULT_EXPIRE;
    
    private boolean isPersistKey = false;
    
    // TODO 处理isInvalid状态,针对invalid会话抛出IllegalStateException异常
    private boolean isInvalid = false;
    
	public CachedRedisHttpSession(HttpSession session, String token) {
		super(session);
		this.token = token;
		try {
			this.tokenBytes = token.getBytes(DEFAULT_CHARSET);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		initialize();
	}

	@Override
	public void initialize() {
		//获取Redis中所有"Attribute"，缓存到本地
		Map<byte[], byte[]> all = RedisUtil.getJedis().hgetAll(tokenBytes);
		try {
			if (all != null) {
				Set<Entry<byte[],byte[]>> set = all.entrySet();
				for (Entry<byte[],byte[]> entry : set) {
					String key = new String(entry.getKey(), DEFAULT_CHARSET);
					Object value = deserizlize(entry.getValue());
					attributes.put(key, value);
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		//设置maxInactiveInterval
		Integer expire = (Integer) attributes.get(REDIS_EXPIRE_KEY);
		if (expire != null) {
			this.maxInactiveInterval = expire;
			if (expire == -1) {
				isPersistKey = true;
			}
		}
	}

	@Override
	public void commit() {
		//提交Session属性到Redis中
		Map<byte[], byte[]> serializedMap = new HashMap<>();
		try {
			for (Entry<String, Object> entry : attributes.entrySet()) {
				byte[] key = entry.getKey().getBytes(DEFAULT_CHARSET);
				byte[] serializedValue = serialize(entry.getValue());
				serializedMap.put(key, serializedValue);
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		RedisUtil.getJedis().hmset(tokenBytes, serializedMap);
		setExpireToRedis();
	}

	@Override  
    public int getMaxInactiveInterval() { 
        return maxInactiveInterval;
    }
	
	@Override  
    public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.isPersistKey = false;
		this.maxInactiveInterval = maxInactiveInterval;
    	attributes.put(REDIS_EXPIRE_KEY, maxInactiveInterval);
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
		RedisUtil.getJedis().del(tokenBytes);
	}
	
	private void setExpireToRedis() {
		if (maxInactiveInterval == -1) {
			if (!isPersistKey) {
				RedisUtil.getJedis().persist(tokenBytes);
				isPersistKey = true;
			}
		}
		else {
			RedisUtil.getJedis().expire(tokenBytes, maxInactiveInterval);
		}
	}
	
    private static byte [] serialize(Object obj) {
    	if (obj == null) {
    		return null;
    	}
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Object deserizlize(byte[] byt) {
    	if (byt == null) {
    		return null;
    	}
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
