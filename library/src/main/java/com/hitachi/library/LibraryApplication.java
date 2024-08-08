package com.hitachi.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
		// used before V2__Inserting_Admin.sql to get the value of the admin password
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String hashedPassword = encoder.encode("admin123");
//		System.out.println("hashedPassword: "+hashedPassword);
	}

}
