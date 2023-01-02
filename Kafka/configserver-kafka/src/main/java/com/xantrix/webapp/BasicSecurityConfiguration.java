package com.xantrix.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

 

@Configuration
public class BasicSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	 @Value("${sicurezza.userpwd}")  
	 String user_password;

	 @Value("${sicurezza.adminpwd}")  
	 String admin_password; 
	 
	 @Bean
	 public static BCryptPasswordEncoder passwordEncoder()
	 {
		return new BCryptPasswordEncoder();
	 }
	 
	 @Autowired
	 public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception 
	 {
	    auth
	        .inMemoryAuthentication()
	        .withUser("user")
	        .password(new BCryptPasswordEncoder().encode(user_password))
	        .roles("USER")
	        .and()
	        .withUser("admin")
	        .password(new BCryptPasswordEncoder().encode(admin_password))
	        .roles("USER", "ACTUATOR");
	 }
	 
	 @Override
		protected void configure(HttpSecurity http) throws Exception 
		{
		    http
		        .csrf().disable()
		        .httpBasic()
		        .and()
		        .authorizeRequests()
		        .antMatchers("/actuator/**").hasAuthority("ROLE_ACTUATOR")
		        .anyRequest().authenticated()
		        .and()
				.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler());
		}
		
		@Bean
		public AccessDeniedHandler accessDeniedHandler() 
		{
			 return new CustomAccessDeniedHandler();
		}
}
