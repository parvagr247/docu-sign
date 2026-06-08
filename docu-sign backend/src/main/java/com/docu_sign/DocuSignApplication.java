package com.docu_sign;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocuSignApplication {


	public static void main(String[] args) {


    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	
	SpringApplication.run(DocuSignApplication.class, args);
	}

}
