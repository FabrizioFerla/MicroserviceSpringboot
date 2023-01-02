package com.xantrix.webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.dtos.PrezzoDto;
import com.xantrix.webapp.entities.DettListini;
import com.xantrix.webapp.services.PrezziService;

import lombok.extern.java.Log;

@RestController
@RequestMapping("/api/prezzi")
@Log
public class PrezziController
{
	@Autowired
	private PrezziService prezziService;
	
	@Autowired
	private AppConfig Config;
	
	// ------------------- SELECT PREZZO CODART ------------------------------------
	@RefreshScope
	@GetMapping(value = {"/{codart}/{idlist}", "/{codart}"})
	public ResponseEntity<Double> getPriceCodArt(@PathVariable("codart") String CodArt, 
			@PathVariable("idlist") Optional<String> optIdList)  
	{
		double retVal = 0;

		String IdList = (optIdList.isPresent()) ? optIdList.get() : Config.getListino();
		
		log.info("Listino di Riferimento: " + IdList);
		
		DettListini prezzo =  prezziService.SelPrezzo(CodArt, IdList);
		
		if (prezzo != null)
		{
			log.info("Prezzo Articolo: " + prezzo.getPrezzo());
			
			double sconto = Config.getSconto();
			
			if (sconto > 0)
			{
				log.info("Attivato Sconto: " + sconto + "%");
				
				retVal = prezzo.getPrezzo() * (1 - (sconto / 100));
				retVal *= 100; 
				retVal = Math.round(retVal);
				retVal /= 100;
			}
			
		}
		else
		{
			log.warning("Prezzo Articolo Assente!!");
			return new ResponseEntity<Double>(retVal, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Double>(retVal, HttpStatus.OK);
	}
	
	@RefreshScope
	@GetMapping(value = {"info/{codart}/{idlist}", "info/{codart}"})
	public ResponseEntity<PrezzoDto> getPriceCodArt2(@PathVariable("codart") String CodArt, 
			@PathVariable("idlist") Optional<String> optIdList)  
	{
		PrezzoDto retVal = new PrezzoDto();

		String idList = (optIdList.isPresent()) ? optIdList.get() : Config.getListino();
		
		
		log.info("Listino di Riferimento: " + idList);
		
		DettListini prezzo =  prezziService.SelPrezzo(CodArt, idList);
		
		if (prezzo != null)
		{
			log.info("Prezzo Articolo: " + prezzo.getPrezzo());
			
			double sconto = Config.getSconto();
			int tipo =  Config.getTipo();
			
			retVal.setCodArt(CodArt);
			retVal.setPrezzo(prezzo.getPrezzo());
			retVal.setSconto(sconto);
			retVal.setTipo(tipo);
		}
		else
		{
			log.warning("Prezzo Articolo Assente!!");
			return new ResponseEntity<PrezzoDto>(retVal, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<PrezzoDto>(retVal, HttpStatus.OK);
	}
	 
	// ------------------- DELETE PREZZO LISTINO ------------------------------------
	@RequestMapping(value = "/elimina/{codart}/{idlist}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePrice(@PathVariable("codart") String CodArt, 
			@PathVariable("idlist") String IdList)
	{
		log.info(String.format("Eliminazione prezzo listino %s dell'articolo %s",IdList,CodArt));

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		prezziService.DelPrezzo(CodArt, IdList);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Prezzo Eseguita Con Successo");
		
		log.info("Eliminazione Prezzo Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
	

}
