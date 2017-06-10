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

public class RedisHttpSession extends HttpSessionWrapper {  
  
    private final String token;
    private int maxInactiveInterval;
    private static int DEFAULT_EXPIRE = 60 * 30;
    
    public RedisHttpSession(HttpSession session,String token) {  
        super(session);  
        this.token = token;
        maxInactiveInterval = DEFAULT_EXPIRE;
    }
  
	@Override  
    public int getMaxInactiveInterval() {  
        return maxInactiveInterval;
    }
	
	@Override  
    public void setMaxInactiveInterval(int maxInactiveInterval) {
		if (maxInactiveInterval == -1) {
			//永不过期
		}
        this.maxInactiveInterval = maxInactiveInterval;
    }
	
    @Override  
    public String getId() {  
        return token;  
    }
    
  	@Override  
    public Enumeration<String> getAttributeNames() {
  		Set<byte[]> keys = RedisUtil.getJedis().hkeys(token.getBytes());
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
    	Set<byte[]> keys = RedisUtil.getJedis().hkeys(token.getBytes());
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
		RedisUtil.getJedis().hset(token.getBytes(), name.getBytes(), serialize(value));
		setExpireToRedis();
	}  

	@Override  
	public Object getAttribute(String name) {
		if (StringUtils.isBlank(name))
			return null;
		byte[] value = RedisUtil.getJedis().hget(token.getBytes(), name.getBytes());
		Object object = deserizlize(value);
		setExpireToRedis();
		return object;
	}
	
	@Override  
	public void invalidate() {
		RedisUtil.getJedis().del(token.getBytes());
	}
	
	private void setExpireToRedis() {
		RedisUtil.getJedis().expire(token.getBytes(), maxInactiveInterval);
	} 
	
    public static byte [] serialize(Object obj) {
    	if (obj == null)
    		return null;
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object deserizlize(byte[] byt) {
    	if (byt == null)
    		return null;
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}