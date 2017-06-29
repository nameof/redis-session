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

import cas.cdao.CacheDao;
import cas.utils.RedisUtil;

/**
 * 基于Redis的缓存数据访问层
 * @author ChengPan
 */
public class RedisCacheDao implements CacheDao{

	private static final String DEFAULT_CHARSET = "UTF-8";
	
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
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return attributes;
	}

	@Override
	public Object getAttribute(String key, String fieldName) {
		byte[] value = null;
		try {
			value = RedisUtil.getJedis().hget(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET));
			return deserizlize(value);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void setAttribute(String key, String fieldName, Object value) {
		try {
			RedisUtil.getJedis().hset(key.getBytes(DEFAULT_CHARSET), fieldName.getBytes(DEFAULT_CHARSET),
					serialize(value));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Enumeration<String> getAttributeNames(String key) {
		Set<byte[]> keys = null;
		try {
			keys = RedisUtil.getJedis().hkeys(key.getBytes(DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException e) {
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
		} catch (UnsupportedEncodingException e) {
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
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setExpire(String key, int expire) {
		try {
			RedisUtil.getJedis().expire(key.getBytes(DEFAULT_CHARSET), expire);
		} catch (UnsupportedEncodingException e) {
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
