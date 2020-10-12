package com.vinapex.licensingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
public class ApexLicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApexLicensingServiceApplication.class, args);
	}

}
