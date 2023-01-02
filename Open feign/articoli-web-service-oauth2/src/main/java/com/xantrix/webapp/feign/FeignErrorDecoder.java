package com.xantrix.webapp.feign;

import org.springframework.stereotype.Component;

import com.xantrix.webapp.exceptions.NotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.java.Log;

@Component
@Log
public class FeignErrorDecoder implements ErrorDecoder
{
	@Override
	public Exception decode(String methodKey, Response response) 
	{
		switch (response.status())
        {
            case 400:
                    log.warning("Codice Stato " + response.status() + ", Metodo = " + methodKey);
            case 404:
            {
            		log.warning("Errore occorso nel Feign client inviando una Richiesta HTTP. Codice Stato: " + 
            					response.status() + ", Metodo = " + methodKey);
            		
                    return new NotFoundException("Prezzo Articolo Non Trovato!"); 
            }
            default:
                return new Exception(response.reason());
        } 
	}
} 
 