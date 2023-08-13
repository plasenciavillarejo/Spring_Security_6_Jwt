package com.spring.app.models.entity;

import java.io.Serializable;

public class LoginRequest implements Serializable {
	
	private static final long serialVersionUID = 8007665158817694502L;
	
	private String username;
	private String password;

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
}
