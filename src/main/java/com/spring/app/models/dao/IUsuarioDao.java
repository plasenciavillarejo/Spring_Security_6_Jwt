package com.spring.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.models.entity.Usuario;

public interface IUsuarioDao extends JpaRepository<Usuario, Long> {

	public Usuario findByUsername(String userame);
}
