package com.spring.app.models.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ManyToAny;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 20)
	private String username;

	@Column(length = 60)
	private String password;

	private Boolean enabled;

	private String nombre;

	private String apellido;

	@Column(unique = true)
	private String email;

	@ManyToMany(fetch = FetchType.LAZY)
	/* Está configuración nos siver para crear una tabla intermedia entre usuario y roles que maneje ambas fk relacionadas entre ellas
	  Indicamos el nombre de la clase intermedia -> usuarios_to_roles
	   Llave foránea de la clase principal -> joinColumns = @JoincColumn
	   	name=user_id,
	   	LLave foranea de roles -> inverseJoinColumns = @JoinColumn(name="role_id")
	  Indicamos un Constraint para que el el user_id y el role_id sean únicos y no sean repetidos -> Un usuario no puede tener un rol repetido
	   	*/
	@JoinTable(name = "usuarios_in_role",
	joinColumns = @JoinColumn(name="usuario_id"),
	inverseJoinColumns = @JoinColumn(name="role_id"),
	uniqueConstraints = {
			@UniqueConstraint(columnNames = {"usuario_id","role_id"})
			}
	)
	private List<Role> roles;
			
	public Long getId() {
		return id;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private static final long serialVersionUID = 1062930741616099556L;

}
