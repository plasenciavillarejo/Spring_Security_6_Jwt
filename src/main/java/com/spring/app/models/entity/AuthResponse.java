package com.spring.app.models.entity;

import java.io.Serializable;

// Clase encargada de devolver el token y el refreshToken si el usuario se autentica correctamente.
public class AuthResponse implements Serializable {
	
	private static final long serialVersionUID = 4011901016526579230L;

	private String mensaje;
	
	private String token;
	
	private String refreshToken;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	
	
}
