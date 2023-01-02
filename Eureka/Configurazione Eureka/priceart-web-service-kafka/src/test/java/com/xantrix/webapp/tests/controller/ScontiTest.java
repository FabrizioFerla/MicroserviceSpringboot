package com.xantrix.webapp.tests.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.DettListini;
import com.xantrix.webapp.entities.Listini;
import com.xantrix.webapp.repository.ListinoRepository;

@TestPropertySource(properties = {"profilo = list100", "seq = 1", "ramo = promo"})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ScontiTest 
{
	private MockMvc mockMvc;

    @Autowired
	private WebApplicationContext wac;
    
    @Autowired
    private ListinoRepository listinoRepository;
    
    String IdList = "100";
    String CodArt = "002000301";
    Double Prezzo = 1.00;

    @BeforeAll
	public void setup()
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		//Inserimento Dati Listino 100
		InsertDatiListino(IdList,"Listino Test 100",CodArt,Prezzo);
    }
	
	private void InsertDatiListino(String IdList, String Descrizione, String CodArt, double Prezzo)
	{
		
		Listini listinoTest = new Listini(IdList,Descrizione,"No");
    	
    	Set<DettListini> dettListini = new HashSet<>();
    	DettListini dettListTest = new DettListini(CodArt,Prezzo, listinoTest);
    	dettListini.add(dettListTest);
    	
    	listinoTest.setDettListini(dettListini);
    	
    	listinoRepository.save(listinoTest);
	}



	@Test
	@Order(1)
	public void testGetPrzCodArt() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/prezzi/" + CodArt)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").value("0.9")) //<-- Prezzo con applicato lo sconto del 10%
			.andReturn();
	}
	
	@Test
	@Order(2)
	public void testDelPrezzo() throws Exception
	{
		String Url = String.format("/api/prezzi/elimina/%s/%s/",CodArt,IdList);
		
		mockMvc.perform(MockMvcRequestBuilders.delete(Url)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message").value("Eliminazione Prezzo Eseguita Con Successo"))
				.andDo(print());
	}
	
	@AfterAll
	public void ClearData()
	{
		Optional<Listini> listinoTest = listinoRepository.findById(IdList);
    	listinoRepository.delete(listinoTest.get());
	}
}
