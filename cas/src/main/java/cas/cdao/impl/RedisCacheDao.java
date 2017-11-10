package cas.cdao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.cdao.CacheDao;
import cas.utils.RedisUtil;

/**
 * 基于Redis的缓存数据访问层
 * @author ChengPan
 */
public class RedisCacheDao implements CacheDao{

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheDao.class);
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		Map<String, Object> attributes = new HashMap<>();
		Map<byte[], byte[]> all = RedisUtil.getJedis().hgetAll(key.getBytes(DEFAULT_CHARSET));
		if (all != null) {
			Set<Entry<byte[],byte[]>> set = all.entrySet();
			for (Entry<byte[],byte[]> entry : set) {
				String byteKey = new String(entry.getKey(), DEFAULT_CHARSET);
				Object value = deserizlize(entry.getValue());
				attributes.put(byteKey, value);
			}
		}
		return attributes;
	}
	
	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		//提交Session属性到缓存中
		Map<byte[], byte[]> serializedMap = new HashMap<>();
		for (Entry<String, Object> entry : attributes.entrySet()) {
			byte[] byteKey = entry.getKey().getBytes(DEFAULT_CHARSET);
			byte[] serializedValue = serialize(entry.getValue());
			serializedMap.put(byteKey, serializedValue);
		}
		RedisUtil.getJedis().hmset(key.getBytes(DEFAULT_CHARSET), serializedMap);	
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		byte[] value = null;
		value = RedisUtil.getJedis().hget(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET));
		return deserizlize(value);
	}


	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		RedisUtil.getJedis().hset(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET),
				serialize(value));
	}


	@Override
	public Enumeration<String> getAttributeNames(String key) {
		Set<byte[]> keys = null;
		keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		
		if (keys == null) {
			return null;
		}
		
		Set<String> skeys = new HashSet<String>();
		for (byte[] k : keys) {
			skeys.add(new String(k));
		}
		return new Vector<String>(skeys).elements();
	}


	@Override
	public String[] getValueNames(String key) {
		Set<byte[]> keys = null;
		keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		
		if (keys == null) {
			return null;
		}
		
		String[] skeys = new String[keys.size()];
		int i = 0;
		for (byte[] k : keys) {
			skeys[i] = new String(k);
			i++;
		}
		return skeys;
	}

	@Override
	public void del(String key) {
		RedisUtil.getJedis().del(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public void setExpire(String key, int expire) {
		RedisUtil.getJedis().expire(key.getBytes(DEFAULT_CHARSET), expire);
	}

	@Override
	public Long getExpire(String key) {
		return RedisUtil.getJedis().ttl(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public void setPersist(String key) {
		RedisUtil.getJedis().persist(key.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public boolean exists(String key) {
		return RedisUtil.getJedis().exists(key.getBytes(DEFAULT_CHARSET));
	}
	


	@Override
	public void setWithExpire(String key, Object value, int expire) {
		RedisUtil.getJedis().setex(key.getBytes(DEFAULT_CHARSET), expire, serialize(value));
	}


	@Override
	public Object get(String key) {
		byte[] byteVal = RedisUtil.getJedis().get(key.getBytes(DEFAULT_CHARSET));
		return deserizlize(byteVal);
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
        	logger.error("IOException thrown from RedisCacheDao on object serialize", e);
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
        catch (IOException e) {
        	logger.error("IOException thrown from RedisCacheDao on object deserizlize", e);
        } catch (ClassNotFoundException e) {
        	logger.error("ClassNotFoundException thrown from RedisCacheDao on object deserizlize", e);
		}
        return null;
    }
}