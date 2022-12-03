package com.study.ddd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/home")
	public String getHello() {
		return "hello world";
	}
}
