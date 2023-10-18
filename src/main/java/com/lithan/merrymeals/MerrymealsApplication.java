package com.lithan.merrymeals;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MerrymealsApplication {

	// @GetMapping("/")
    // public String welcome() {
    //     return "Welcome to MerryMeals!";
    // }

    // @GetMapping("/usergoogle")
    // public Principal user(Principal principal) {
    //     System.out.println("Username: " + principal.getName());
    //     return principal;
    // }
	public static void main(String[] args) {
		SpringApplication.run(MerrymealsApplication.class, args);
	}

}
