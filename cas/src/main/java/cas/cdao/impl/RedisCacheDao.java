package cas.cdao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.cdao.CacheDao;
import cas.utils.RedisUtil;

/**
 * 基于Redis的缓存数据访问层
 * @author ChengPan
 */
public class RedisCacheDao implements CacheDao{

	private static final String DEFAULT_CHARSET = "UTF-8";
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheDao.class);
	
	@Override
	public Map<String, Object> getAllAttribute(String key) {
		Map<String, Object> attributes = new HashMap<>();
		try {
			Map<byte[], byte[]> all = RedisUtil.getJedis().hgetAll(key.getBytes(DEFAULT_CHARSET));
			if (all != null) {
				Set<Entry<byte[],byte[]>> set = all.entrySet();
				for (Entry<byte[],byte[]> entry : set) {
					String byteKey = new String(entry.getKey(), DEFAULT_CHARSET);
					Object value = deserizlize(entry.getValue());
					attributes.put(byteKey, value);
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("getAllAttribute");
			throw new RuntimeException(e);
		}
		return attributes;
	}
	

	private void unsupportedEncodingLog(String methodName) {
		logger.error("RedisCacheDao方法{}中key不支持编码格式{}", methodName, DEFAULT_CHARSET);
	}


	@Override
	public void setAllAttributes(String key, Map<String, Object> attributes) {
		//提交Session属性到缓存中
		Map<byte[], byte[]> serializedMap = new HashMap<>();
		try {
			for (Entry<String, Object> entry : attributes.entrySet()) {
				byte[] byteKey = entry.getKey().getBytes(DEFAULT_CHARSET);
				byte[] serializedValue = serialize(entry.getValue());
				serializedMap.put(byteKey, serializedValue);
			}
			RedisUtil.getJedis().hmset(key.getBytes(DEFAULT_CHARSET), serializedMap);
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setAllAttributes");
			throw new RuntimeException(e);
		}	
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		byte[] value = null;
		try {
			value = RedisUtil.getJedis().hget(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET));
			return deserizlize(value);
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("getAttribute");
			throw new RuntimeException(e);
		}
	}


	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		try {
			RedisUtil.getJedis().hset(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET),
					serialize(value));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setAttribute");
			throw new RuntimeException(e);
		}
	}


	@Override
	public Enumeration<String> getAttributeNames(String key) {
		Set<byte[]> keys = null;
		try {
			keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("getAttributeNames");
			throw new RuntimeException(e);
		}
		
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
		try {
			keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("getValueNames");
			throw new RuntimeException(e);
		}
		
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
		try {
			RedisUtil.getJedis().del(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("del");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setExpire(String key, int expire) {
		try {
			RedisUtil.getJedis().expire(key.getBytes(DEFAULT_CHARSET), expire);
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setExpire");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long getExpire(String key) {
		try {
			return RedisUtil.getJedis().ttl(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setExpire");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setPersist(String key) {
		try {
			RedisUtil.getJedis().persist(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setPersist");
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean exists(String key) {
		try {
			return RedisUtil.getJedis().exists(key.getBytes(DEFAULT_CHARSET));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setPersist");
			throw new RuntimeException(e);
		}
	}
	


	@Override
	public void setWithExpire(String key, Object value, int expire) {
		try {
			RedisUtil.getJedis().setex(key.getBytes(DEFAULT_CHARSET), expire, serialize(value));
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("setWithExpire");
			throw new RuntimeException(e);
		}
	}


	@Override
	public Object get(String key) {
		try {
			byte[] byteVal = RedisUtil.getJedis().get(key.getBytes(DEFAULT_CHARSET));
			return deserizlize(byteVal);
		}
		catch (UnsupportedEncodingException e) {
			unsupportedEncodingLog("get");
			throw new RuntimeException(e);
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