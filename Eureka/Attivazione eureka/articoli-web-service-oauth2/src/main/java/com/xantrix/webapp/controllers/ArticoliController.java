package com.xantrix.webapp.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.dtos.PrezzoDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.BindingException;
import com.xantrix.webapp.exceptions.DuplicateException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.feign.PriceClient;
import com.xantrix.webapp.services.ArticoliService;

import feign.FeignException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@RestController
@RequestMapping("api/articoli")
@Log
public class ArticoliController 
{
	@Autowired
	private ArticoliService articoliService;
	
	@Autowired
	private ResourceBundleMessageSource errMessage;
	
	@Autowired
	private PriceClient priceClient;
	
	private double getPriceArt(String CodArt, String IdList, String Header)
	{
		
		double prezzo = 0;
		
		try
		{
			 
			PrezzoDto prezzoDto = (IdList != null) ? 
					priceClient.getPriceArt2(Header, CodArt, IdList) : 
					priceClient.getDefPriceArt2(Header, CodArt);
			 
			log.info("Prezzo Articolo " + prezzoDto.getCodArt() + ": " + prezzoDto.getPrezzo());
			
			if (prezzoDto.getSconto() > 0)
			{
				log.info("Attivato Sconto: " + prezzoDto.getSconto() + "%");
				
				prezzo = prezzoDto.getPrezzo() * (1 - ( prezzoDto.getSconto() / 100));
				prezzo *= 100; 
				prezzo = Math.round(prezzo);
				prezzo /= 100;
			}
			else 
			{
				prezzo = prezzoDto.getPrezzo();
			}
			
			
			/*
			prezzo = (IdList != null) ? 
					priceClient.getPriceArt(Header, CodArt, IdList) : 
					priceClient.getDefPriceArt(Header, CodArt);
			 
			log.info("Prezzo Articolo " + CodArt + ": " + prezzo);
			*/

		}
		catch(FeignException ex)
		{
			log.warning(String.format("Errore: %s", ex.getLocalizedMessage()));
		}
		
		return prezzo;
	}
	
	@SneakyThrows
	@GetMapping(value = {"/cerca/barcode/{ean}","/cerca/barcode/{ean}/{idlist}"}, produces = "application/json")
	public ResponseEntity<ArticoliDto> listArtByEan(
			@PathVariable("ean") String Ean,
			@PathVariable("idlist") Optional<String> optIdList,
			HttpServletRequest httpRequest)
	{
		log.info(String.format("****** Otteniamo l'articolo con barcode %s *******", Ean));
		
		String IdList = (optIdList.isPresent()) ? optIdList.get() : "";
		String AuthHeader = httpRequest.getHeader("Authorization");
		
		ArticoliDto articolo = articoliService.SelByBarcode(Ean);
		
		if (articolo == null)
		{
			String ErrMsg = String.format("Il barcode %s non e' stato trovato!", Ean);
			
			log.warning(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		else
		{
			articolo.setPrezzo(this.getPriceArt(articolo.getCodArt(), IdList, AuthHeader));
		}
		
		return new ResponseEntity<ArticoliDto>(articolo, HttpStatus.OK);
	}
	
	@SneakyThrows
	@GetMapping(value = {"/cerca/codice/{codart}","/cerca/codice/{codart}/{idlist}"}, produces = "application/json")
	public ResponseEntity<ArticoliDto> listArtByCodArt(
			@PathVariable("codart") String CodArt,
			@PathVariable("idlist") Optional<String> optIdList,
			HttpServletRequest httpRequest)	
	{
		String AuthHeader = httpRequest.getHeader("Authorization");
		String IdList = (optIdList.isPresent()) ? optIdList.get() : "";
		
		log.info("****** Otteniamo l'articolo con codice " + CodArt + " *******");
		
		ArticoliDto articolo = articoliService.SelByCodArt(CodArt);
		
		if (articolo == null)
		{
			String ErrMsg = String.format("L'articolo con codice %s non e' stato trovato!", CodArt);
			
			log.warning(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		else
		{
			articolo.setPrezzo(this.getPriceArt(articolo.getCodArt(), IdList, AuthHeader));
		}
		
		return new ResponseEntity<ArticoliDto>(articolo, HttpStatus.OK);
		
	}
	
	@SneakyThrows
	@GetMapping(value = {"/cerca/descrizione/{filter}","/cerca/descrizione/{filter}/{idlist}"}, produces = "application/json")
	public ResponseEntity<List<ArticoliDto>> listArtByDesc(
			@PathVariable("filter") String Filter,
			@PathVariable("idlist") Optional<String> optIdList,
			HttpServletRequest httpRequest)
	{
		log.info("****** Otteniamo gli articoli con Descrizione: " + Filter + " *******");
		
		String AuthHeader = httpRequest.getHeader("Authorization");
		String IdList = (optIdList.isPresent()) ? optIdList.get() : "";
		
		List<ArticoliDto> articoli = articoliService.SelByDescrizione(Filter);
		
		if (articoli.isEmpty())
		{
			String ErrMsg = String.format("Non e' stato trovato alcun articolo avente descrizione %s", Filter);
			
			log.warning(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
			
		}
		else
		{
			articoli.forEach(f -> f.setPrezzo(this.getPriceArt(f.getCodArt(), IdList, AuthHeader)));
		}
		
		return new ResponseEntity<List<ArticoliDto>>(articoli, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/inserisci", produces = "application/json")
	@SneakyThrows
	public ResponseEntity<InfoMsg> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult) 
	{
		log.info("Salviamo l'articolo con codice " + articolo.getCodArt());
		
		//controllo validità dati articolo
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			log.warning(MsgErr);
			
			throw new BindingException(MsgErr);
		}
		
		//Disabilitare se si vuole gestire anche la modifica 
		ArticoliDto checkArt =  articoliService.SelByCodArt(articolo.getCodArt());
		
		if (checkArt != null)
		{
			String MsgErr = String.format("Articolo %s presente in anagrafica! "
					+ "Impossibile utilizzare il metodo POST", articolo.getCodArt());
			
			log.warning(MsgErr);
			
			throw new DuplicateException(MsgErr);
		}
		
		articoliService.InsArticolo(articolo);
		
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(), 
				"Inserimento Articolo Eseguita con successo!"), HttpStatus.CREATED);
	}
	
	// ------------------- MODIFICA ARTICOLO ------------------------------------
	@SneakyThrows
	@RequestMapping(value = "/modifica", method = RequestMethod.PUT)
	public ResponseEntity<InfoMsg> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult)
	{
		log.info("Modifichiamo l'articolo con codice " + articolo.getCodArt());
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			log.warning(MsgErr);

			throw new BindingException(MsgErr);
		}
		
		ArticoliDto checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt == null)
		{
			String MsgErr = String.format("Articolo %s non presente in anagrafica! "
					+ "Impossibile utilizzare il metodo PUT", articolo.getCodArt());
			
			log.warning(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}
		
		articoliService.InsArticolo(articolo);
		
		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(), 
				"Modifica Articolo Eseguita con successo!"), HttpStatus.CREATED);
	}
	
	// ------------------- ELIMINAZIONE ARTICOLO ------------------------------------
	@SneakyThrows
	@DeleteMapping(value = "/elimina/{codart}", produces = "application/json" )
	public ResponseEntity<?> deleteArt(@PathVariable("codart") String CodArt)
	{
		log.info("Eliminiamo l'articolo con codice " + CodArt);
		
		Articoli articolo = articoliService.SelByCodArt2(CodArt);
		
		if (articolo == null)
		{
			String MsgErr = String.format("Articolo %s non presente in anagrafica!",CodArt);
			
			log.warning(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}
		
		articoliService.DelArticolo(articolo);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Articolo " + CodArt + " Eseguita Con Successo");
		
		return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.OK);
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
