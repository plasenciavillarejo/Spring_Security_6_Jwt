package com.spring.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {

	@PostMapping(value ="/prueba")
	public String welcome() {
		return "Endopoint protegido";
	}
	
}
