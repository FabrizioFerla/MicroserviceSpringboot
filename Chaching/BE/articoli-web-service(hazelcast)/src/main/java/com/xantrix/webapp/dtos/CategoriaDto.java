package com.xantrix.webapp.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class CategoriaDto implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8090444846402357034L;
	private int id;
	private String descrizione;
}
