package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Habilitar la programación de tareas
public class CreditosApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditosApplication.class, args);
	}

}
