package com.spring.app.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.jwt.JwtService;
import com.spring.app.models.entity.AuthResponse;
import com.spring.app.models.entity.Usuario;
import com.spring.app.utility.Utilidades;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManagerExterno;
	private JwtService jwtService;
	
	private Utilidades utilidades = new Utilidades();
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, RequestMatcher requestMatcher) {
		this.authenticationManagerExterno = authenticationManager;
		this.jwtService = jwtService;
		setRequiresAuthenticationRequestMatcher(requestMatcher);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		Usuario usuario = null;
		
		try {
			usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		}catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
				
		return authenticationManagerExterno.authenticate(new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getPassword()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		User user = (User) authResult.getPrincipal();

		Usuario usuario = utilidades.usuarioDevuelto(user);

		String token = jwtService.getToken(usuario);
		String refreshToken = jwtService.refreshTokenGenerate(usuario);

		response.addHeader("Authorization", token);

		Map<String, Object> httpResponse = new HashMap<>();
		httpResponse.put("token", token);
		httpResponse.put("refreshToken", refreshToken);
		httpResponse.put("Message", "Autenticacion Correcta");
		httpResponse.put("Username", user.getUsername());

		AuthResponse usuarioRespuesta = new AuthResponse();
		// usuarioRespuesta.setMensaje("Inicio de sesión correcto");
		usuarioRespuesta.setToken(token);
		usuarioRespuesta.setRefreshToken(refreshToken);

		response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().flush();
	}
	    
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mensaje", "Error de autenticación: username o password incorrecto!");
		body.put("error", failed.getMessage());

		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}
	
}
