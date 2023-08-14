package com.spring.app.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.spring.app.models.entity.TokenListaNegra;
import com.spring.app.models.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRET_KEY = "5125561565265675ASDA5SDFASDA61234JKJ12312222222222222222222312388989898989890890980890ADUSUOPISUDOIPUAIOPIUOPDIUPOAPUIOAUIPUIPOAPUIUIPFJK98";
	
	/* ## 1.- INI: Método encargado de generar el token cuando se entra por el endpoint /register  ## */
	/* ############################################################################################## */
	public String getToken(Usuario user) {
		return getToken(new HashMap<>(),user);
	}

	public String refreshTokenGenerate(Usuario usuario) {
		return getRefreshToken(new HashMap<>(),usuario);
	}
	
	private String getToken(Map<String, Object> claims, Usuario user) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getUsername())
				// Fecha de Creación
				.setIssuedAt(new Date(System.currentTimeMillis()))
				// Fecha de Expiración; (12 hora tarda el token)
				//.setExpiration(new Date(System.currentTimeMillis() + 12L * 60 * 60 * 1000))
				// Un minuto de expiración
				.setExpiration(new Date(System.currentTimeMillis() + 60 * 1000))
				// Pasamos la firma
				// Dado que estás utilizando el algoritmo HMAC-SHA256, que requiere una clave de al menos 256 bits (32 bytes), debes asegurarte de que la longitud de tu clave sea de al menos 32 bytes.
				.signWith(getKey(), SignatureAlgorithm.HS256)
				// Crea el objeto y lo serializa
				.compact();
	}

	
	private String getRefreshToken(Map<String, Object> claims, Usuario user) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getUsername())
				// Fecha de Creación
				.setIssuedAt(new Date(System.currentTimeMillis()))
				// Fecha de Expiración; (se suma 1 dia)
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
				// Pasamos la firma
				// Dado que estás utilizando el algoritmo HMAC-SHA256, que requiere una clave de al menos 256 bits (32 bytes), debes asegurarte de que la longitud de tu clave sea de al menos 32 bytes.
				.signWith(getKey(), SignatureAlgorithm.HS256)
				// Crea el objeto y lo serializa
				.compact();
	}
	
	private Key getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		// Creamos una nueva instancia de nuestra secret key
		return Keys.hmacShaKeyFor(keyBytes);
	}

	
	/* ## FIN: Método encargado de generar el token cuando se entra por el endpoint /register  ## */
	/* ########################################################################################## */
	
	/* ## 2.- INI: Método encargados de validar el token para acceder a los endpoints con seguridad ## */
	/* ############################################################################################### */
	
	public String getUsernameFromToken(String token) {
		return obtenerClaimConcreto(token,Claims::getSubject);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		// Validamos que el userDetails corresponde con el usuario de el token
		String usernameToken = getUsernameFromToken(token);
		return (usernameToken.equals(userDetails.getUsername()) && !tokenExpirado(token));
	}
	
	// Validar la fecha de expiracion en el token
	private Date fechaExpiracion(String token) {
		return obtenerClaimConcreto(token,Claims::getExpiration);
	}
	
	// Validar token si ha expirado
	private boolean tokenExpirado(String token) {
		return fechaExpiracion(token).before(new Date());
	}
	
	// Validación Token desde los claims 
	private Claims obtenerClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	// Obtenemos un claim en particular utilizando un objeto genérico
	public <T> T obtenerClaimConcreto(String token, Function<Claims, T> claimsResolver) {
		Claims claims = null;
		try { 
		 claims = obtenerClaims(token);
		} catch (ExpiredJwtException  e) {
			// Si el token ha expirado procedemos a capturar dicho token y almacenarlo en la lista negra
			TokenListaNegra.addToBlacklist(token);
			throw new ExpiredJwtException(null, claims, "Token Expirado");
		}
		return claimsResolver.apply(claims);
	}
	
	/* ## FIN: Método encargados de validar el token para acceder a los endpoints con seguridad ## */
	/* ########################################################################################### */
	
}
