package com.xantrix.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component 
@Data
public class EnvValue 
{
	@Value("${jasypt.encryptor.password}")
	private String password;
}
