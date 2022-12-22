package com.xantrix.webapp.tests.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.utility.JsonData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

 

@SpringBootTest()
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
public class TestSecurityRoleAdmin
{
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
	public void setup()
	{
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.defaultRequest(get("/")
				.with(user("Admin").roles("ADMIN"))) //Ruoli Attivati
				.apply(springSecurity()) //Attiva la sicurezza
				.build();

	}
	
	private String ApiBaseUrl = "/api/articoli";
	
	@Order(1)
	@Test
	public void testListArtByCodArt() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/codice/002000301")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(JsonData.GetArtData())) 
				.andReturn();
	}
	
	
	@Order(2)
	@Test
	public void testInsArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData.GetTestArtData())
				.accept(MediaType.APPLICATION_JSON))
		
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.message").value("Inserimento Articolo Eseguita con successo!"))

				.andDo(print());
	}
	
	@Order(3)
	@Test
	public void testDelArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.delete(ApiBaseUrl + "/elimina/123Test")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message").value("Eliminazione Articolo 123Test Eseguita Con Successo"))
				.andDo(print());
	}
	 
}
