package cas.custom.component;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class HttpSessionWrapper implements HttpSession,Serializable {  
  
	
	private static final long serialVersionUID = -7569479249899543476L;
	
	private HttpSession session;
	
    private final long creationTime = System.currentTimeMillis();
    
    public HttpSessionWrapper(HttpSession session) {
        this.session = session;  
    }
    
    @Override
    public long getCreationTime() {
        return creationTime;
    }  
  
    @Override  
    public String getId() {  
        return this.session.getId();  
    }  
  
    @Override  
    public long getLastAccessedTime() {  
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
        return this.session.isNew();  
    }  
}  