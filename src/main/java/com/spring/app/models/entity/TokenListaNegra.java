package com.spring.app.models.entity;

import java.util.HashSet;
import java.util.Set;


// Clase encargada de almacenar los tokens que ha expirado
public class TokenListaNegra {

	private static Set<String> blacklistedTokens = new HashSet<>();

    public static void addToBlacklist(String token) {
    	    	
    	if(blacklistedTokens.isEmpty()) {
    		blacklistedTokens.add(token);
    	} else {
	    	for(String tken: blacklistedTokens) {
	    		if(!tken.equalsIgnoreCase(token)) {
	    			blacklistedTokens.add(token);
	    		}
	    	}
    	}
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    public static void removeExpiredTokens() {
        // Implement logic to remove expired tokens from the blacklist
    }
	
}
