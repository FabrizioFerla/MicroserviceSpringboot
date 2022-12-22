package com.xantrix.webapp.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.model.Utenti;
 
import com.xantrix.webapp.service.UtentiService;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

@RestController
@RequestMapping(value = "/api/utenti")
@Log
public class UtentiController
{
	@Autowired
	UtentiService utentiService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private ResourceBundleMessageSource errMessage;
	
	@GetMapping(value = "/cerca/tutti")
	public List<Utenti> getAllUser()
	{
		log.info("Otteniamo tutti gli utenti");

		return utentiService.SelTutti();
	}
	
	@GetMapping(value = "/cerca/userid/{userId}")
	@SneakyThrows
	public Utenti getUtente(@PathVariable("userId") String UserId) 
	{
		log.info("Otteniamo l'utente " + UserId);
		
		Utenti utente = utentiService.SelUser(UserId);
		
		if (utente == null)
		{
			String ErrMsg = String.format("L'utente %s non e' stato trovato!", UserId);
			
			log.warning(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		
		return utente;
	}
	
	// ------------------- INSERIMENTO / MODIFICA UTENTE ------------------------------------
	@PostMapping(value = "/inserisci")
	@SneakyThrows
	public ResponseEntity<InfoMsg> addNewUser(@Valid @RequestBody Utenti utente, 
		BindingResult bindingResult)
	{
		
		Utenti checkUtente = utentiService.SelUser(utente.getUserId());
		
		if (checkUtente != null)
		{
			utente.setId(checkUtente.getId());
			log.info("Modifica Utente");
		}
		else 
		{
			log.info("Inserimento Nuovo Utente");
		}

		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			log.warning(MsgErr);

			throw new BindingException(MsgErr);
		}
		
		String encodedPassword = passwordEncoder.encode(utente.getPassword());
		utente.setPassword(encodedPassword);
		utentiService.Save(utente);

		return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(), 
				String.format("Inserimento Utente %s Eseguita Con Successo", utente.getUserId())), HttpStatus.CREATED);
	}

	// ------------------- ELIMINAZIONE UTENTE ------------------------------------
	@DeleteMapping(value = "/elimina/{id}")
	@SneakyThrows
	public ResponseEntity<?> deleteUser(@PathVariable("id") String UserId)
	{
		log.info("Eliminiamo l'utente con id " + UserId);

		Utenti utente = utentiService.SelUser(UserId);

		if (utente == null)
		{
			String MsgErr = String.format("Utente %s non presente in anagrafica! ",UserId);
			
			log.warning(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}

		utentiService.Delete(utente);
		
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Utente " + UserId + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
}
