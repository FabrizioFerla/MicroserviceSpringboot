package com.xantrix.webapp.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoliRepository;

import lombok.extern.java.Log;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames={"articoli"})
@Log
public class ArticoliServiceImpl implements ArticoliService
{
	@Autowired
	ArticoliRepository articoliRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	CacheManager cacheManager;

	@Override
	@Cacheable
	public List<ArticoliDto> SelByDescrizione(String descrizione) 
	{
		String filter = "%" + descrizione.toUpperCase() + "%";
		
		List<Articoli> articoli = articoliRepository.selByDescrizioneLike(filter);
		
		return ConvertToDto(articoli);
	}

	@Override
	@Cacheable
	public List<ArticoliDto> SelByDescrizione(String descrizione, Pageable pageable) 
	{
		String filter = "%" + descrizione.toUpperCase() + "%";
		
		List<Articoli> articoli = articoliRepository.findByDescrizioneLike(filter, pageable);
		
		return ConvertToDto(articoli);
		
		
	}
	
	@Override
	public Articoli SelByCodArt2(String codart) 
	{	 
		return  articoliRepository.findByCodArt(codart);
	}
	
	@Override
	@Cacheable(value = "articolo",key = "#codart",sync = true)
	public ArticoliDto SelByCodArt(String codart) 
	{
		Articoli articoli = this.SelByCodArt2(codart);
		
		return this.ConvertToDto(articoli);
	}

	@Override
	@Cacheable(value = "barcode",key = "#barcode",sync = true)
	public ArticoliDto SelByBarcode(String barcode) 
	{
		Articoli articoli = articoliRepository.selByEan(barcode);
		
		return this.ConvertToDto(articoli);
	}
	
	@Override
	@Transactional
	@Caching(evict = { 
			@CacheEvict(cacheNames="articoli", allEntries = true),
			//@CacheEvict(cacheNames="barcode",key = "#articolo.barcode[0].barcode"),
			@CacheEvict(cacheNames="articolo",key = "#articolo.codArt")
			})
	public void DelArticolo(Articoli articolo) 
	{
		articoliRepository.delete(articolo);
		
		this.EvictCache(articolo.getBarcode());
	}

	@Override
	@Transactional
	@Caching(evict = { 
			@CacheEvict(cacheNames="articoli", allEntries = true),
			//@CacheEvict(cacheNames="barcode",key = "#articolo.barcode[0].barcode"),
			@CacheEvict(cacheNames="articolo",key = "#articolo.codArt")
			})
	public void InsArticolo(Articoli articolo) 
	{
		articolo.setDescrizione(articolo.getDescrizione().toUpperCase());
		
		articoliRepository.save(articolo);
		
		this.EvictCache(articolo.getBarcode());
	}
	
	private void EvictCache(Set<Barcode> Ean) 
	{
		 Ean.forEach((Barcode barcode) -> {
			 log.info("Eliminazione cache barcode " + barcode.getBarcode());
			 
			 cacheManager.getCache("barcode").evict(barcode.getBarcode());
	     });
	}
	
	@Override
	public void CleanCaches() 
	{
		Collection<String> items = cacheManager.getCacheNames();
		
		items.forEach((item) -> {
			log.info(String.format("Eliminazione cache %s", item));
			
			cacheManager.getCache(item).clear();
		});
		
	}
	
	private ArticoliDto ConvertToDto(Articoli articoli)
	{
		ArticoliDto articoliDto = null;
		
		
		if (articoli != null)
		{
			articoliDto =  modelMapper.map(articoli, ArticoliDto.class);
		}
		
		return articoliDto;
	}
	
	private List<ArticoliDto> ConvertToDto(List<Articoli> articoli)
	{
		List<ArticoliDto> retVal = articoli
		        .stream()
		        .map(source -> modelMapper.map(source, ArticoliDto.class))
		        .collect(Collectors.toList());
		
		return retVal;
	}

	


}
