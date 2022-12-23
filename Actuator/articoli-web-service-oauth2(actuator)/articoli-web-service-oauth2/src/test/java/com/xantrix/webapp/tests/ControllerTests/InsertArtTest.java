package com.xantrix.webapp.tests.ControllerTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class InsertArtTest
{
	 
    private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	ArticoliRepository articoliRepository;
	
	
	@BeforeEach
	public void setup() throws JSONException, IOException
	{
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.build();	
	}
	
	private String JsonData =  
					"{\r\n"
					+ "    \"codArt\": \"123Test\",\r\n"
					+ "    \"descrizione\": \"Articolo Unit Test Inserimento\",\r\n"
					+ "    \"um\": \"PZ\",\r\n"
					+ "    \"codStat\": \"TESTART\",\r\n"
					+ "    \"pzCart\": 6,\r\n"
					+ "    \"pesoNetto\": 1.75,\r\n"
					+ "    \"idStatoArt\": \"1\",\r\n"
					+ "    \"dataCreaz\": \"2019-05-14\",\r\n"
					+ "    \"barcode\": [\r\n"
					+ "        {\r\n"
					+ "            \"barcode\": \"12345678\",\r\n"
					+ "            \"idTipoArt\": \"CP\"\r\n"
					+ "        }\r\n"
					+ "    ],\r\n"
					+ "    \"ingredienti\": {\r\n"
					+ "		\"codArt\" : \"123Test\",\r\n"
					+ "		\"info\" : \"TEST INGREDIENTI\"\r\n"
					+ "	},\r\n"
					+ "    \"iva\": {\r\n"
					+ "        \"idIva\": 22\r\n"
					+ "    },\r\n"
					+ "    \"famAssort\": {\r\n"
					+ "        \"id\": 1\r\n"
					+ "    }\r\n"
					+ "}";
	
	@Test
	@Order(1)
	public void testInsArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.message").value("Inserimento Articolo Eseguita con successo!"))
				.andDo(print());
		
		assertThat(articoliRepository.findByCodArt("123Test"))
			.extracting(Articoli::getDescrizione)
			.isEqualTo("ARTICOLO UNIT TEST INSERIMENTO");
	}
	
	@Test
	@Order(2)
	public void testErrInsArticolo1() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.code").value(406))
				.andExpect(jsonPath("$.message").value("Articolo 123Test presente in anagrafica! Impossibile utilizzare il metodo POST"))
				.andDo(print());
	}
	
	String ErrJsonData =  
					"{\r\n"
					+ "    \"codArt\": \"123Test\",\r\n"
					+ "    \"descrizione\": \"\",\r\n" //<-- Descrizione Assente
					+ "    \"um\": \"PZ\",\r\n"
					+ "    \"codStat\": \"TESTART\",\r\n"
					+ "    \"pzCart\": 6,\r\n"
					+ "    \"pesoNetto\": 1.75,\r\n"
					+ "    \"idStatoArt\": \"1\",\r\n"
					+ "    \"dataCreaz\": \"2019-05-14\",\r\n"
					+ "    \"barcode\": [\r\n"
					+ "        {\r\n"
					+ "            \"barcode\": \"12345678\",\r\n"
					+ "            \"idTipoArt\": \"CP\"\r\n"
					+ "        }\r\n"
					+ "    ],\r\n"
					+ "    \"ingredienti\": {\r\n"
					+ "		\"codArt\" : \"123Test\",\r\n"
					+ "		\"info\" : \"TEST INGREDIENTI\"\r\n"
					+ "	},\r\n"
					+ "    \"iva\": {\r\n"
					+ "        \"idIva\": 22\r\n"
					+ "    },\r\n"
					+ "    \"famAssort\": {\r\n"
					+ "        \"id\": 1\r\n"
					+ "    }\r\n"
					+ "}";
	
	@Test
	@Order(3)
	public void testErrInsArticolo2() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.post("/api/articoli/inserisci")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ErrJsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.message").value("Il campo Descrizione deve avere un numero di caratteri compreso tra 6 e 80"))
				.andDo(print());
	}
	
	private String JsonDataMod =  
			"{\r\n"
			+ "    \"codArt\": \"123Test\",\r\n"
			+ "    \"descrizione\": \"Articolo Unit Test Modifica\",\r\n" //<-- Descrizione Modificata
			+ "    \"um\": \"PZ\",\r\n"
			+ "    \"codStat\": \"TESTART\",\r\n"
			+ "    \"pzCart\": 6,\r\n"
			+ "    \"pesoNetto\": 1.75,\r\n"
			+ "    \"idStatoArt\": \"1\",\r\n"
			+ "    \"dataCreaz\": \"2019-05-14\",\r\n"
			+ "    \"barcode\": [\r\n"
			+ "        {\r\n"
			+ "            \"barcode\": \"12345678\",\r\n"
			+ "            \"idTipoArt\": \"CP\"\r\n"
			+ "        }\r\n"
			+ "    ],\r\n"
			+ "    \"ingredienti\": {\r\n"
			+ "		\"codArt\" : \"123Test\",\r\n"
			+ "		\"info\" : \"TEST INGREDIENTI\"\r\n"
			+ "	},\r\n"
			+ "    \"iva\": {\r\n"
			+ "        \"idIva\": 22\r\n"
			+ "    },\r\n"
			+ "    \"famAssort\": {\r\n"
			+ "        \"id\": 1\r\n"
			+ "    }\r\n"
			+ "}";
	
	@Test
	@Order(4)
	public void testUpdArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articoli/modifica")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.message").value("Modifica Articolo Eseguita con successo!"))
				.andDo(print());
		
		assertThat(articoliRepository.findByCodArt("123Test"))
			.extracting(Articoli::getDescrizione)
			.isEqualTo("ARTICOLO UNIT TEST MODIFICA");
	}
	
	private String JsonDataMod2 =  
			"{\r\n"
			+ "    \"codArt\": \"Pippo125\",\r\n" //<-- Codice Inesistente
			+ "    \"descrizione\": \"Articolo Unit Test Modifica\",\r\n" //<-- Descrizione Modificata
			+ "    \"um\": \"PZ\",\r\n"
			+ "    \"codStat\": \"TESTART\",\r\n"
			+ "    \"pzCart\": 6,\r\n"
			+ "    \"pesoNetto\": 1.75,\r\n"
			+ "    \"idStatoArt\": \"1\",\r\n"
			+ "    \"dataCreaz\": \"2019-05-14\",\r\n"
			+ "    \"barcode\": [\r\n"
			+ "        {\r\n"
			+ "            \"barcode\": \"12345678\",\r\n"
			+ "            \"idTipoArt\": \"CP\"\r\n"
			+ "        }\r\n"
			+ "    ],\r\n"
			+ "    \"ingredienti\": {\r\n"
			+ "		\"codArt\" : \"123Test\",\r\n"
			+ "		\"info\" : \"TEST INGREDIENTI\"\r\n"
			+ "	},\r\n"
			+ "    \"iva\": {\r\n"
			+ "        \"idIva\": 22\r\n"
			+ "    },\r\n"
			+ "    \"famAssort\": {\r\n"
			+ "        \"id\": 1\r\n"
			+ "    }\r\n"
			+ "}";
	
	@Test
	@Order(5)
	public void testErrUpdArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.put("/api/articoli/modifica")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonDataMod2)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Articolo Pippo125 non presente in anagrafica! Impossibile utilizzare il metodo PUT"))
				.andDo(print());
	}
	
	@Test
	@Order(6)
	public void testDelArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/articoli/elimina/123Test")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("200 OK"))
				.andExpect(jsonPath("$.message").value("Eliminazione Articolo 123Test Eseguita Con Successo"))
				.andDo(print());
	}
	
	@Test
	@Order(7)
	public void testErrDelArticolo() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/articoli/elimina/123Test")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Articolo 123Test non presente in anagrafica!"))
				.andDo(print());
	}
	
	
	
}
