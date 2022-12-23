package com.xantrix.webapp.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class BarcodeDto implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7885944990935907091L;

	private String barcode;
	
	private String idTipoArt;
}
