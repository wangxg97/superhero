package com.next.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class ClusterController {

	@GetMapping("/cluster")
	public Object cluster() {
		return "192.168.1.3";
	}
}
