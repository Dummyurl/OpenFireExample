package com.jj.investigation.openfire.bean;

import java.io.Serializable;

/**
 * 用户信息
 * @author Administrator
 *
 */
public class User implements Serializable {
	private int id;
	private String username;
	private String plainPassword;
	private String desc;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPlainPassword() {
		return plainPassword;
	}
	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", plainPassword=" + plainPassword + ", desc=" + desc
				+ "]";
	}
	
	
	
}
