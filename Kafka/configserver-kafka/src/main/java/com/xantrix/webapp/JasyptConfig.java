package com.xantrix.webapp;


import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.java.Log;

@Configuration
@Log
public class JasyptConfig 
{
	@Value("${jasypt.encryptor.password}")
	private String password = ""; 
	
	public SimpleStringPBEConfig getSimpleStringPBEConfig()
	{
		log.info("PWD: " + password);
		
		final SimpleStringPBEConfig pbeConfig = new SimpleStringPBEConfig();
		
		//https://www.devglan.com/online-tools/jasypt-online-encryption-decryption
		
		pbeConfig.setPassword(password);  //TODO Trasformare in variabile d'ambiente
		pbeConfig.setAlgorithm("PBEWithMD5AndDES");
        pbeConfig.setKeyObtentionIterations("1000");
        pbeConfig.setPoolSize("1");
        pbeConfig.setProviderName("SunJCE");
        pbeConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        pbeConfig.setStringOutputType("base64");
        
        return pbeConfig;
	}
	
	 @Bean(name = "jasyptStringEncryptor")
	 public StringEncryptor encryptor() 
	 {
	        final PooledPBEStringEncryptor pbeStringEncryptor = new PooledPBEStringEncryptor();
	        pbeStringEncryptor.setConfig(getSimpleStringPBEConfig());
	 
	        return pbeStringEncryptor;
	 }
}
