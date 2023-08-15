package com.spring.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
		
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private AuthService authService;
	
	// 1.- Se encarga de crear un usuario y posteriormente se le asigna un token
	@PostMapping(value ="/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest register)  {		
		return ResponseEntity.ok(authService.register(register));
	}
	
	/* 2.- Controlador encargado de recibir el token y validarlo. (Por ahora no está funcionando se explica el detalle en la siguiente líneas)
		Nota: Como tengo un filtro CustomAuthenticationFilter.java que extiende de UsernamePasswordAuthenticationFilter, el es el encargado de autenticar el usuario
		para meterlo en el contexto de spring security y nunca se va a ejecutar este método. Si quito el filtro si entraría por este método. 
	*/
	@PostMapping(value ="/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest login) {			
		return ResponseEntity.ok(authService.login(login));
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		LOGGER.info("El usuario authenticado es : {}", authentication.getName());
		LOGGER.info("Contiene los siguientes roles: {}", authentication.getAuthorities());
		
		
		return ResponseEntity.ok(authService.refreshTokenGenerate(request, response));
	}

}
