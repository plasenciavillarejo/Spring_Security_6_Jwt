package com.spring.app.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.spring.app.models.entity.Role;
import com.spring.app.models.entity.Usuario;

@Component
public class Utilidades {

	// Función encargada de envolver el usuario registrado en el contexto de spring en 'Usuario' para poder generar el token
	public Usuario usuarioDevuelto(UserDetails userDetails) {
		
		Usuario usuarioAplicacion= new Usuario();
		usuarioAplicacion.setUsername(userDetails.getUsername());
		usuarioAplicacion.setPassword(userDetails.getPassword());				
		
		// Recuperamos los roles
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		
		List<Role> roles = new ArrayList<>();
		for (GrantedAuthority authority : authorities) {
		    Role nombreRol = new Role();
		    nombreRol.setNombre(authority.getAuthority());
		    roles.add(nombreRol);
		}
		usuarioAplicacion.setRoles(roles);
		
		return usuarioAplicacion;
	}
	
}
