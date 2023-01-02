package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ArticoliRepository extends PagingAndSortingRepository<Articoli, String>
{
	Articoli findByCodArt(String codArt);
	
	List<Articoli> findByDescrizioneLike(String descrizione, Pageable pageable);
	
	@Query(value = "SELECT * FROM ARTICOLI WHERE DESCRIZIONE LIKE :desArt", nativeQuery = true)
	List<Articoli> selByDescrizioneLike(@Param("desArt") String descrizione);
	
	//JPQL
	@Query(value="SELECT a FROM Articoli a JOIN a.barcode b WHERE b.barcode IN (:ean)")
	Articoli selByEan(@Param("ean") String ean);
	
	@Query(value = "SELECT COUNT(*) FROM ARTICOLI", nativeQuery = true)
	int countArts();
	
	/*
	//SQL
	@Query(value = "SELECT * FROM articoli a JOIN barcode b ON a.codart = b.codart WHERE b.barcode = :ean", 
		nativeQuery = true )
	Articoli SelByEan(@Param("ean") String ean);
	*/
	
	
}
