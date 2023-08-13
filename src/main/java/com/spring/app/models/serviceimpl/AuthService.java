package com.spring.app.models.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
		
		// Transformarmos el objeto UserDetails en nuestro Usuario de otra forma no podemos castearlo para poder crear el token
		Usuario usuarioAplicacion= new Usuario();
		usuarioAplicacion.setUsername(userDetails.getUsername());
		usuarioAplicacion.setPassword(userDetails.getPassword());				
		
		// Recuperamos los roles
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		
		List<Role> roles = new ArrayList<>();
		for (GrantedAuthority authority : authorities) {
		    Role nombreRol = new Role();
		    nombreRol.setNombre(authority.getAuthority());
		    roles.add(nombreRol);
		}
		usuarioAplicacion.setRoles(roles);
		
		String token = "";
		if(userDetails != null) {
			token = jwtService.getToken(usuarioAplicacion);
		}
		
		AuthResponse usuarioRespuesta = new AuthResponse();
		usuarioRespuesta.setToken(token);
		
		return usuarioRespuesta;
	}
	
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
		usuarioRespuesta.setToken(jwtService.getToken(usuarioNuevo));
		
		return usuarioRespuesta;
	}
	
	
	
}
