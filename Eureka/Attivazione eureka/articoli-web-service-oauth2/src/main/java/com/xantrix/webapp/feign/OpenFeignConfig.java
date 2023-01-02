package com.xantrix.webapp.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Retryer;

@Configuration
@EnableFeignClients
public class OpenFeignConfig 
{
	@Bean
    Logger.Level feignLoggerLevel() 
	{
        return Logger.Level.FULL;
    }
	
	@Bean
	public ErrorAndRetryDecoder getFeignErrorDecoder()
	{
		return new ErrorAndRetryDecoder();  //FeignErrorDecoder();
	}
	
	@Bean
	public Retryer retryer()
	{
		return new CustomRetryer();
	}

}
