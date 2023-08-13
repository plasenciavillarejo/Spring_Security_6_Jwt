package com.spring.app.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.spring.app.jwt.JwtAuthenticationFilter;
import com.spring.app.models.serviceimpl.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	@Autowired
	private AuthenticationEventPublisher eventPublisher;
	
	@Autowired
	private UserDetailsService usuarioService;

	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
	@Autowired
	private JwtAuthenticationFilter authenticationFilter;		
	
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); 
    }
    
	@Autowired
	private CustomAuthenticationProvider authenticationProvider;
	
    
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Validación propia de spring security
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder())
		.and()
		//auth.authenticationProvider(authenticationProvider);
		// Registramos nuestro evento para AuthenticationSuccesErrorHandler.java
		.authenticationEventPublisher(eventPublisher);
	}  
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			// Para poder acceder a h2 debemos deshabilitar los frame de la cabecera
			.headers().frameOptions().sameOrigin()
			.and()
			.authorizeHttpRequests()
			.antMatchers("/authentication/**").permitAll()
			.anyRequest().authenticated()
			.and()
			// Inhabilitamos la sesiones
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// Autenticación basada en JWT
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
			
		return http.build();
	}

	
	
}