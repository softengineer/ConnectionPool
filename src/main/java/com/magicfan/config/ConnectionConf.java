package com.magicfan.config;

import java.lang.reflect.Field;

/**
 *  ConnectionConf class
 *  
 *  - Represent a connection configuration
 *
 */

public class ConnectionConf {
	
	/**
	 * connection url information
	 */
	private String url = null;
	
	/**
	 * minimum pool size
	 */
	private int minpoolsize = 10;
	
	/**
	 * maximum pool size
	 */
	private int maxpoolsize = 10;
	
	/**
	 * connection username
	 */
	private String username = null;
	
	/**
	 * connection password
	 */
	private String password = null;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getMinpoolsize() {
		return minpoolsize;
	}
	
	public void setMinpoolsize(String minpoolsize) {
		this.minpoolsize = Integer.parseInt(minpoolsize);
	}
	
	public int getMaxpoolsize() {
		return maxpoolsize;
	}
	
	public void setMaxpoolsize(String maxpoolsize) {
		this.maxpoolsize = Integer.parseInt(maxpoolsize);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean valid() {
		return true;
	}
	
	public String toString () {
		try {
			StringBuilder builder = new StringBuilder();
			Field fields [] = this.getClass().getFields();
			for (Field field : fields) {
				builder.append(field.getName()).append(" = ").append(field.get(this).toString());
			}
			return builder.toString();
		} catch(Exception e) {
			return "";
		}
	}
}
