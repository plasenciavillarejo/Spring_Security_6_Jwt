package com.spring.app.models.entity;

import java.io.Serializable;

public class AuthResponse implements Serializable {
	
	private static final long serialVersionUID = 4011901016526579230L;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	String token;
}
