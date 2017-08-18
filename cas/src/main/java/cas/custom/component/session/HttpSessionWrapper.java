package cas.custom.component.session;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpSession包装类
 * 
 * @author ChengPan
 */
@SuppressWarnings("deprecation")
public abstract class HttpSessionWrapper implements HttpSession,Serializable {  
  
	
	private static final long serialVersionUID = -7569479249899543476L;
	
	protected final HttpSession session;
	
    private final long creationTime = System.currentTimeMillis();
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected boolean isNew = false;
    
    protected boolean isInvalid = false;
    
    public HttpSessionWrapper(HttpSession session) {
        this.session = session;
        setNew(session.isNew());
    }
    
    @Override
    public long getCreationTime() {
    	checkValid();
        return creationTime;
    }  
  
    @Override  
    public String getId() {  
        return this.session.getId();  
    }  
  
    @Override  
    public long getLastAccessedTime() {  
    	//TODO 可以直接使用受控于容器实现的的HttpSession.lastAccessedTime
    	//但造成了对容器HttpSession的依赖性，webapp无法完全去除HttpSession
    	//除去lastAccessedTime外，HttpSessionWrapper的实现类所有方法都可以不依赖原始HttpSession
        return this.session.getLastAccessedTime();  
    }  
  
    @Override  
    public ServletContext getServletContext() {  
        return this.session.getServletContext();  
    }  
  
    @Override  
    public void setMaxInactiveInterval(int interval) {  
        this.session.setMaxInactiveInterval(interval);  
    }  
  
    @Override  
    public int getMaxInactiveInterval() {
        return this.session.getMaxInactiveInterval();  
    }
  
    @Override  
    public HttpSessionContext getSessionContext() {  
    	throw new UnsupportedOperationException("getSessionContext");
    }  
  
    @Override  
    public Object getAttribute(String name) {  
        return this.session.getAttribute(name);  
    }  
  
    @Override  
    public Object getValue(String name) {  
        return this.getAttribute(name);
    }  
  
    @Override  
    public Enumeration<String> getAttributeNames() {  
        return this.session.getAttributeNames();  
    }  
  
	@Override  
    public String[] getValueNames() {  
        return this.session.getValueNames();
    }  
  
    @Override  
    public void setAttribute(String name, Object value) {  
        this.session.setAttribute(name,value);  
    }  
  
    @Override  
    public void putValue(String name, Object value) {  
        this.setAttribute(name, value);
    }  
  
    @Override  
    public void removeAttribute(String name) {  
        this.session.removeAttribute(name);  
    }  
  
    @Override
    public void removeValue(String name) {  
        this.removeAttribute(name);
    }  
  
    @Override
    public void invalidate() {  
        this.session.invalidate();  
    }  
  
    @Override
    public boolean isNew() {  
    	checkValid();
        return this.isNew;
    }
    
    public void setNew(boolean isNew) {
    	checkValid();
    	this.isNew = isNew;
    }
	
	public boolean isInvalid() {
		return this.isInvalid;
	}
	
	/**
	 * 如果当前会话已被invalidate，则抛出IllegalStateException异常
	 */
	protected void checkValid() {
		if(isInvalid) {
			throw new IllegalStateException("attempt to access session data after the session has been invalidated!");
		}
	}
	
	/**
	 * 获取Servlet原始的HttpSession
	 * @return 原始的HttpSession
	 */
	public HttpSession getHttpSession() {
		return this.session;
	}
}  