package com.spring.app.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


// El filtro solo se ejecuta una vez por solicitud HTTP, aunque existe multiples filtros en una cadena.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	
	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		// Recuperar el token del request
		String token = getTokenFromRequest(request);
		
		String username = "";
		
		// Si el token es vacío o es la pagina de login donde se crea el token debo dejar que continue, de otra forma cuando expire el token nunca podre crear otro nuevo.
		if(token == null || request.getRequestURI().equals("/login")) {
			filterChain.doFilter(request, response);
			return;
		}
			
		// Obtenemos el token desde nuestra clase jwtService
		username = jwtService.getUsernameFromToken(token);
		
		// Si no encontramos el username y no está validado en el contexto de spring security vamos a buscarlo en la base de datos con el userDetailService
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailService.loadUserByUsername(username);			
			if(jwtService.isTokenValid(token,userDetails)) {
				// Si es valido, procedemos actualizar el SecurityContextHolder a traves de el UsernamePasswordAuthenticationToken
				UsernamePasswordAuthenticationToken actualizarTokenSecurity = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 
				
				// Ahora testeamos el details
				actualizarTokenSecurity.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// Almacenamos en el contexto de spring security
				SecurityContextHolder.getContext().setAuthentication(actualizarTokenSecurity);
			}			
		}		
		filterChain.doFilter(request, response);		
	}

	
	public String getTokenFromRequest(HttpServletRequest request) {
		// Obtenemos de la cabecera la authorization
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		// Si se cumple viene el token
		if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			// Retornamos desde el caracter 7 hasta el final que contiene el token eliminadno el -> "Bearer "
			return authHeader.substring(7);
		}
		return null;
	}
	
}
