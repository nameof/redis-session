package cas.cdao;

import java.util.Enumeration;
import java.util.Map;

public interface CacheDao {

	Map<String, Object> getAllAttribute(String key);
	
	Object getAttribute(String key, String fieldName);
	
	void setAttribute(String key, String fieldName, Object value);
	
	Enumeration<String> getAttributeNames(String key);
	
	String[] getValueNames(String key);
	
	void del(String key);
	
	void setExpire(String key, int expire);
}
