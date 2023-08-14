package com.spring.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.models.entity.AuthResponse;
import com.spring.app.models.entity.LoginRequest;
import com.spring.app.models.entity.RegisterRequest;
import com.spring.app.models.serviceimpl.AuthService;

@RestController
@RequestMapping(value = "/authentication")
public class AuthController {
		
	@Autowired
	private AuthService authService;
	
	// 1.- Se encarga de crear un usuario y posteriormente se le asigna un token
	@PostMapping(value ="/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest register)  {		
		return ResponseEntity.ok(authService.register(register));
	}
	
	// 2.- Controlador encargado de recibir el token y validarlo
	@PostMapping(value ="/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest login) {			
		return ResponseEntity.ok(authService.login(login));
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return ResponseEntity.ok(authService.refreshTokenGenerate(request, response));
	}

}
