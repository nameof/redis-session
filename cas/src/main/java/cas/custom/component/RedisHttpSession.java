package cas.custom.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cas.utils.RedisUtil;
// TODO 写注释
public class RedisHttpSession extends HttpSessionWrapper implements
		CustomSessionProcessor {

	/**
	 * 默认过期时间为30分钟
	 */
	private static final int DEFAULT_EXPIRE = 60 * 30;

	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 一旦调用setMaxInactiveInterval，就会把maxInactiveInterval值存入Redis，
	 * 以REDIS_EXPIRE_KEY为token的fieldKey进行存储
	 */
	private static final String REDIS_EXPIRE_KEY = "maxInactiveInterval";

	private static final byte[] REDIS_EXPIRE_KEY_BYTE_VALUE = REDIS_EXPIRE_KEY.getBytes();

	private final String token;

	private final byte[] tokenBytes;

	private int maxInactiveInterval = DEFAULT_EXPIRE;

	private boolean isPersistKey = false;
	
    // TODO 处理isInvalid状态,针对invalid会话抛出IllegalStateException异常
	private boolean isInvalid = false;

	public RedisHttpSession(HttpSession session, String token) {
		super(session);
		this.token = token;
		this.tokenBytes = token.getBytes();
		initialize();
	}

	@Override
	public void initialize() {
		// 从Redis中读取maxInactiveInterval信息
		byte[] originalExpireBytes = RedisUtil.getJedis().hget(tokenBytes, REDIS_EXPIRE_KEY_BYTE_VALUE);
		if (originalExpireBytes != null) {
			Integer originalExpire = (Integer) deserizlize(originalExpireBytes);
			this.maxInactiveInterval = originalExpire;
			if (originalExpire == -1) {
				isPersistKey = true;
			}
		}
	}

	@Override
	public void commit() {
		setExpireToRedis();
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
		RedisUtil.getJedis().hset(tokenBytes, REDIS_EXPIRE_KEY_BYTE_VALUE,
				serialize(Integer.valueOf(maxInactiveInterval)));
	}

	@Override
	public String getId() {
		return token;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Set<byte[]> keys = RedisUtil.getJedis().hkeys(tokenBytes);
		if (keys == null) {
			return null;
		}
		Set<String> skeys = new HashSet<String>();
		for (byte[] key : keys) {
			skeys.add(new String(key));
		}
		return new Vector<String>(skeys).elements();
	}

	@Override
	public String[] getValueNames() {
		Set<byte[]> keys = RedisUtil.getJedis().hkeys(tokenBytes);
		if (keys == null) {
			return null;
		}
		String[] skeys = new String[keys.size()];
		int i = 0;
		for (byte[] key : keys) {
			skeys[i] = new String(key);
			i++;
		}
		return skeys;
	}

	@Override
	public void setAttribute(String name, Object value) {
		RedisUtil.getJedis()
				.hset(tokenBytes, name.getBytes(), serialize(value));
	}

	@Override
	public Object getAttribute(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		byte[] value = RedisUtil.getJedis().hget(tokenBytes, name.getBytes());
		Object object = deserizlize(value);
		return object;
	}

	@Override
	public void invalidate() {
		this.isInvalid = true;
		RedisUtil.getJedis().del(tokenBytes);
	}
	
	public boolean isInvalid() {
		return this.isInvalid;
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

	private static byte[] serialize(Object obj) {
		if (obj == null) {
			return null;
		}
		ObjectOutputStream obi = null;
		ByteArrayOutputStream bai = null;
		try {
			bai = new ByteArrayOutputStream();
			obi = new ObjectOutputStream(bai);
			obi.writeObject(obj);
			byte[] byt = bai.toByteArray();
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
		ObjectInputStream oii = null;
		ByteArrayInputStream bis = null;
		bis = new ByteArrayInputStream(byt);
		try {
			oii = new ObjectInputStream(bis);
			Object obj = oii.readObject();
			return obj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}