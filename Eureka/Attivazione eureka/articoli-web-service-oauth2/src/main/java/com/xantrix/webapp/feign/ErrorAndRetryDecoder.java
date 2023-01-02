package com.xantrix.webapp.feign;

import org.springframework.stereotype.Component;

//import com.xantrix.webapp.exceptions.NotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.java.Log;

@Component
@Log
public class ErrorAndRetryDecoder implements ErrorDecoder
{
	private final ErrorDecoder defaultErrorDecoder = new Default();
	
	@Override
	public Exception decode(String methodKey, Response response) 
	{
		if (response.status() >= 400 && response.status() <= 499)
		{
			switch (response.status())
	        {
	            case 400:
	                    log.warning("Codice Stato " + response.status() + ", Metodo = " + methodKey);
	            case 404:
	            {
	            		log.warning("Errore occorso nel Feign client inviando una Richiesta HTTP. Codice Stato: " + 
	            					response.status() + ", Metodo = " + methodKey);
	            		
	                    //return new NotFoundException("Prezzo Articolo Non Trovato!"); 
	            }
	        } 
		}
		else if (response.status() >= 500)
		{
			log.warning("Codice Stato " + response.status() + ", Metodo = " + methodKey);
			//return new RetryableException();
		}
		 
		return defaultErrorDecoder.decode(methodKey, response);
	}

}
