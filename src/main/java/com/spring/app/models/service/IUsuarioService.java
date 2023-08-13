package com.spring.app.models.service;

import com.spring.app.models.entity.Usuario;

public interface IUsuarioService {
	
	public Usuario save(Usuario usuario);
	
	public Usuario findByUsername(String username);
	
}
