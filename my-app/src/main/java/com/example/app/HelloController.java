package com.example.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class HelloController {
	private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
    public HelloController() {
	}

	@GetMapping("/v1/hello")
    public ResponseEntity<Object> sayHello_v1() {
		return new ResponseEntity<>("hello world v1 !!", HttpStatus.OK);
    }
	

	@GetMapping("/v2/hello")
    public ResponseEntity<Object> sayHello_v2() {
		return new ResponseEntity<>("hello world v2 !!", HttpStatus.OK);
    }
	
}
