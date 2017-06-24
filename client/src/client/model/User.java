package client.model;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String passwd;
	
	public User() {
		
	}
	
	public User(String name, String passwd) {
		super();
		this.name = name;
		this.passwd = passwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", passwd=" + passwd + "]";
	}
	
	
}
