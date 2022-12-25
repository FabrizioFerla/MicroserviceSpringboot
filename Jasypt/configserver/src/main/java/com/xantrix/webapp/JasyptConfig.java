package com.xantrix.webapp;


import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig 
{
	public static SimpleStringPBEConfig getSimpleStringPBEConfig()
	{
		final SimpleStringPBEConfig pbeConfig = new SimpleStringPBEConfig();
		
		//https://www.devglan.com/online-tools/jasypt-online-encryption-decryption
		
		pbeConfig.setPassword("123_Stella");  //TODO Trasformare in variabile d'ambiente
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
