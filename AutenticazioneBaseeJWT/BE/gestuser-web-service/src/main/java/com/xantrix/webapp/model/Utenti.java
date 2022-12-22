package com.xantrix.webapp.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "utenti")
@Data
public class Utenti
{
	@Id
	private String id;
	
	@Indexed(unique = true)
	@Size(min = 5, max = 80, message = "{Size.Utenti.userId.Validation}")
	@NotNull(message = "{NotNull.Articoli.userId.Validation}")
	private String userId;
	
	@Size(min = 8, max = 80, message = "{Size.Utenti.password.Validation}")
	private String password;
	
	private String attivo = "Si";
	
	@NotNull(message = "{NotNull.Utenti.ruoli.Validation}")
	private List<String> ruoli;
	
}
