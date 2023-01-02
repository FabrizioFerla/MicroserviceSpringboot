package com.xantrix.webapp.security;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import lombok.SneakyThrows;

@KeycloakConfiguration
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter 
{
    @Autowired
    @SneakyThrows
    public void configureGlobal(AuthenticationManagerBuilder auth)
    {
 
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() 
    {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }
    
    private static final String[] ADMIN_MATCHER = { "/api/articoli/inserisci/**", 
			"/api/articoli/modifica/**", "/api/articoli/elimina/**" };

    @Override
    protected void configure(HttpSecurity http) 
    		throws Exception 
    {
        super.configure(http);
        
        http.csrf().disable()
        .authorizeRequests()
        .antMatchers(ADMIN_MATCHER)
        .hasAnyRole("admin")
		.anyRequest().authenticated()
		.and()
		.exceptionHandling()
		.accessDeniedHandler(accessDeniedHandler());
    }
    
    @Override
	public void configure(WebSecurity webSecurity) throws Exception 
	{
		webSecurity.ignoring()
				.antMatchers(HttpMethod.OPTIONS, "/**");		
	}
    
	@Bean
	public AccessDeniedHandler accessDeniedHandler() 
	{
		 return new CustomAccessDeniedHandler();
	}
}
