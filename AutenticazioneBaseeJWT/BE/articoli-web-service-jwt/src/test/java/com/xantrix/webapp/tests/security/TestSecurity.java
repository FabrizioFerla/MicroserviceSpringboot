package com.xantrix.webapp.tests.security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.xantrix.webapp.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest()
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
public class TestSecurity
{
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
	public void setup() throws JSONException, IOException
	{
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(springSecurity()) //Attiva la sicurezza
				.build();

	}
	
	private String ApiBaseUrl = "/api/articoli";
	
	@Order(1)
	@Test
	@WithAnonymousUser
	public void testSecurityErrlistArt() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/codice/002000301")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andReturn();
	}  
	
	@Order(2)
	@Test
	@WithMockUser(username="Nicola",roles={"USER"})
	public void testSecurityErrlistArt2() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/codice/002000301")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	}  
}
