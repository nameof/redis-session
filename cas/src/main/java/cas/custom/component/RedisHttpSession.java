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

import redis.clients.jedis.Jedis;
import cas.utils.RedisUtil;

public class RedisHttpSession extends HttpSessionWrapper {  
  
    private String token;
    private int expire;
    private int DEFAULT_EXPIRE = 60 * 30;
    
    public RedisHttpSession(HttpSession session,String token) {  
        super(session);  
        this.token = token;
        expire = DEFAULT_EXPIRE;
    }
  
	@Override  
    public int getMaxInactiveInterval() {  
        return expire;
    }
	
	@Override  
    public void setMaxInactiveInterval(int expire) {
		if (expire == -1) {
			//永不过期
		}
        this.expire = expire;
    }
	
    @Override  
    public String getId() {  
        return token;  
    }
    
  	@Override  
    public Enumeration<String> getAttributeNames() {
  		Set<byte[]> keys = RedisUtil.getJedis().hkeys(token.getBytes());
  		Set<String> skeys = new HashSet<String>();
  		for(byte[] key : keys){
  			skeys.add(new String(key));
  		}
        return new Vector<String>(skeys).elements();  
    }

    @Override  
	public String[] getValueNames() {
    	Set<byte[]> keys = RedisUtil.getJedis().hkeys(token.getBytes());
    	String[] skeys = new String[keys.size()];
    	int i = 0;
  		for(byte[] key : keys){
  			skeys[i] = new String(key);
  		}
		return skeys;
	} 
	
	@Override  
	public void setAttribute(String name, Object value) {
		RedisUtil.getJedis().hset(token.getBytes(), name.getBytes(), serialize(value));
		setExpire();
	}  

	@Override  
	public Object getAttribute(String name) {
		if (StringUtils.isBlank(name))
			return null;
		byte[] value = RedisUtil.getJedis().hget(token.getBytes(), name.getBytes());
		Object object = deserizlize(value);
		setExpire();
		return object;
	} 
	
	private void setExpire() {
		RedisUtil.getJedis().expire(token.getBytes(), expire);
	} 
	
    public static byte [] serialize(Object obj){
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
    
    public static Object deserizlize(byte[] byt){
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