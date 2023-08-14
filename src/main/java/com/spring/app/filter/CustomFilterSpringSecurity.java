package com.spring.app.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.jwt.JwtService;
import com.spring.app.models.entity.Usuario;
import com.spring.app.models.serviceimpl.UsuarioServiceImpl;

/* Este método attemptAuthentication se encarga de intentar autenticar al usuario. Si no estás realizando una autenticación adicional aquí y solo deseas acceder a la información del usuario autenticado,
  podrías considerar no sobrescribir este método en absoluto. En cambio, podrías acceder a la información del usuario autenticado en otros componentes de tu aplicación donde sea necesario.
*/

public class CustomFilterSpringSecurity extends AbstractAuthenticationProcessingFilter {
	
	Logger LOGGER = LoggerFactory.getLogger(CustomFilterSpringSecurity.class);
	
	public CustomFilterSpringSecurity(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		
		Usuario userEntity = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		
		LOGGER.info("Usuario logueado: {} ", userEntity.getUsername());
		LOGGER.info("Password logueado: {} ", userEntity.getPassword());
				
		//Usuario usuario = usuarioServiceImpl.findByUsername(userEntity.getUsername());		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword());
		
		
		Usuario usu = new Usuario();
		usu.setNombre(userEntity.getUsername());
		usu.setPassword(userEntity.getPassword());
		
		//jwtService.getToken(usu);
		
		return getAuthenticationManager().authenticate(authenticationToken);
	}
	
}
