package com.spring.app.models.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.app.jwt.JwtService;
import com.spring.app.models.entity.AuthResponse;
import com.spring.app.models.entity.LoginRequest;
import com.spring.app.models.entity.RegisterRequest;
import com.spring.app.models.entity.Role;
import com.spring.app.models.entity.Usuario;
import com.spring.app.utility.Utilidades;


// Clase encargada de Registrar un nuevo usuario en BBDD y de hacer el login para autenticarse y devolver el token.
@Service
public class AuthService {

	@Autowired
	private UsuarioServiceImpl usu;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UsuarioServiceImpl usuarioService;
	
	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private Utilidades utilidades;
	
	// 1.- Registra el usuario dentro de nuestra aplicación
	public AuthResponse register(RegisterRequest register) {
		Usuario usuarioNuevo = new Usuario();
		usuarioNuevo.setUsername(register.getUsername());
		usuarioNuevo.setPassword(passwordEncoder.encode(register.getPassword()));
		usuarioNuevo.setNombre(register.getNombre());
		usuarioNuevo.setApellido(register.getApellido());
		usuarioNuevo.setEmail(register.getEmail());
		usuarioNuevo.setEnabled(true);
		
		List<Role> roles = new ArrayList<>();
		for(int i=0; i<1; i++) {
			Role rol = new Role();
			// Rol 1 -> USER
			// Rol 2 -> ADMIN
			rol.setId(2L);
			rol.setNombre("ADMIN");
			roles.add(rol);
		}
		usuarioNuevo.setRoles(roles);
		usu.save(usuarioNuevo);
		
		AuthResponse usuarioRespuesta = new AuthResponse();
		usuarioRespuesta.setMensaje("Usuario registrado correctamente");
		usuarioRespuesta.setToken(jwtService.getToken(usuarioNuevo));
		usuarioRespuesta.setRefreshToken(jwtService.refreshTokenGenerate(usuarioNuevo));
		
		return usuarioRespuesta;
	}
			
	// 2.- Hace login el usuario dentro de nuestra aplicación. (Si utilzo el filtro CustomAuthenticationFilter.java para loguear no entra aquí)
	public AuthResponse login(LoginRequest login) {
		
		/* 1.- Realiza el proceso de autenticación. Luego, el objeto authentication contendrá la información de autenticación resultante, que se puede usar posteriormente para tomar decisiones de autorización 
		  y acceder a los detalles del usuario autenticado.  */
		Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
		
		/* 2.- Se procede a localizar el usuario en BBDD */
		Usuario userDetails = usuarioService.findByUsername(login.getUsername());
		
		/* 3.- Se extrae los detalles del usuario autenticado (representados por un objeto UserDetails) del objeto Authentication después de que el proceso de autenticación se haya completado correctamente.
		  Esto te permite acceder a los detalles del usuario autenticado, como su nombre de usuario, autoridades y otros atributos, para llevar a cabo acciones de autorización y personalizar la lógica de la 
		  aplicación en función de la identidad del usuario.*/
		UserDetails user = (UserDetails) authentication.getPrincipal();
		
		Usuario usuarioAplicacion = utilidades.usuarioDevuelto(user);
				
		String token = "";
		String refreshToken = "";
		if(userDetails != null) {
			token = jwtService.getToken(usuarioAplicacion);
			refreshToken = jwtService.refreshTokenGenerate(usuarioAplicacion);
		}
		
		AuthResponse usuarioRespuesta = new AuthResponse();
		usuarioRespuesta.setMensaje("Inicio de sesión correcto");
		usuarioRespuesta.setToken(token);
		usuarioRespuesta.setRefreshToken(refreshToken);
		
		return usuarioRespuesta;
	}
			
	// 3.- Refresca el token dentro de la aplicación 
	public AuthResponse refreshTokenGenerate(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		String tokenCabecera = "";
		String usuarioToken = "";
		// Validamos que la cabecera
		if(authHeader != null && !authHeader.startsWith("Bearer ")) {
			throw new Exception("No se encuentra el token dentro de la cabecera");
		}
		tokenCabecera = authHeader.substring(7);
		
		// Obtenemos el userDetail.
		//UserDetails userDetail = userDetailService.loadUserByUsername(usuarioToken);
		
		// Vamos a validar si el token ha expirado.
		//boolean tokenExpirado = jwtService.isTokenValid(usuarioToken, userDetail);
		
		// Obtenemos el usuario a partir de su token
		usuarioToken = jwtService.getUsernameFromToken(tokenCabecera);
		AuthResponse usuarioRespuesta = new AuthResponse();
		
		if(usuarioToken != null) {
			UserDetails usuarioDetails = userDetailService.loadUserByUsername(usuarioToken);		
			
			// Obtengo el usuario autenticado
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			// Transformarmos el objeto UserDetails en nuestro Usuario de otra forma no podemos castearlo para poder crear el token
			Usuario usuarioAplicacion= new Usuario();
			usuarioAplicacion.setUsername(usuarioDetails.getUsername());
			usuarioAplicacion.setPassword(usuarioDetails.getPassword());
			
			List<Role> roles = new ArrayList<>();
			for (GrantedAuthority authority : authentication.getAuthorities()) {
			    Role nombreRol = new Role();
			    nombreRol.setNombre(authority.getAuthority());
			    roles.add(nombreRol);
			}
			usuarioAplicacion.setRoles(roles);
			
			// Validamos que el token es correcto y si lo es procedemos a expirarlo e invalidarlo
			if(jwtService.isTokenValid(tokenCabecera, usuarioDetails)) {
				
				// Revoko el token
				
				// Seteo de nuevo el token nuevo
				usuarioRespuesta.setMensaje("El Token ha sido refrescado correctamente");
				usuarioRespuesta.setToken(jwtService.getToken(usuarioAplicacion));
				usuarioRespuesta.setRefreshToken(jwtService.refreshTokenGenerate(usuarioAplicacion));
			}
		}
		return usuarioRespuesta;	
	}
		
}
