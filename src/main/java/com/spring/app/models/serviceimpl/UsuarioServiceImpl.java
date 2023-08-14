package com.spring.app.models.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.models.dao.IUsuarioDao;
import com.spring.app.models.entity.Usuario;
import com.spring.app.models.service.IUsuarioService;

@Service
public class UsuarioServiceImpl implements  UserDetailsService,IUsuarioService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioServiceImpl.class);
	
	@Autowired
	private IUsuarioDao usuarioDao;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			Usuario usuario = usuarioDao.findByUsername(username);

			// Los roles son de el tipo genérico de la interfaz GrantedAuthority, tenemos
			// que convertir los roles en GrantedAuthority utilizando la api de java 8 'stream()'
			List<GrantedAuthority> authorities = usuario.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getNombre()))
					// Por cada rol vamos a mostrar el nombre de el usuario, como ya lo hemos pasado
					// a Authority(), podremos utilizarlo.
					.peek(authority -> LOGGER.info("Rol identificado: {}", authority.getAuthority()))
					// Convertimos a un tipo list
					.collect(Collectors.toList());

			LOGGER.info("Usuario autenticado en la aplicación: {}", username);

			return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true,
					authorities);

		} catch (UsernameNotFoundException e) {
			LOGGER.error("Error en el login, no existe el usario '" + username + "' en el sistema");
			throw new UsernameNotFoundException(
					"Error en el login, no existe el usario '" + username + "' en el sistema");
		}
	}

	public Usuario save(Usuario usuario) {
		return usuarioDao.save(usuario);
	}


	@Override
	public Usuario findByUsername(String username) {
		return usuarioDao.findByUsername(username);
	}


	
}
