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

import com.spring.app.filter.CustomAuthenticationFilter;
import com.spring.app.filter.CustomAuthenticationProvider;
import com.spring.app.filter.CustomFilterSpringSecurity;
import com.spring.app.jwt.JwtAuthenticationFilter;
import com.spring.app.jwt.JwtService;

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
	private JwtService jwtService;
	
	@Bean
    public RequestMatcher customRequestMatcher() {
		// Intercepta la petición antes de que se logue el usuario.
		return new AntPathRequestMatcher("/authentication/login");
    }
	
    
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Validación propia de spring security
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder());
		//.and()
		//auth.authenticationProvider(authenticationProvider);
		// Registramos nuestro evento para AuthenticationSuccesErrorHandler.java
		//.authenticationEventPublisher(eventPublisher);
	}  
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
	
		/*
		CustomAuthenticationFilter c = new CustomAuthenticationFilter();
		c.setAuthenticationManager(authenticationManager);
		c.setFilterProcessesUrl("/authentication/login");
		*/
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
			// Validación de forma personalizada
			.addFilter(new CustomAuthenticationFilter(authenticationManager, jwtService, customRequestMatcher()))
			//.addFilterBefore(new CustomFilterSpringSecurity(customRequestMatcher()),UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
			
		return http.build();
	}

	
	
}
