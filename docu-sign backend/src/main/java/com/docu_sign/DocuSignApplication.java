package com.docu_sign;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DocuSignApplication {


	public static void main(String[] args) {


    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	
	SpringApplication.run(DocuSignApplication.class, args);
	}

}
