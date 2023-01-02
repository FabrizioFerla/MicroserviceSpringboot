package com.xantrix.webapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.xantrix.webapp.dtos.PrezzoDto;

@FeignClient(name = "PriceArtService", url="localhost:5071", configuration = OpenFeignConfig.class)
public interface PriceClient 
{
	@GetMapping(value = "/api/prezzi/{codart}")
	public double getDefPriceArt(@RequestHeader("Authorization") String AuthHeader, 
			@PathVariable("codart") String CodArt);
	
	@GetMapping(value = "/api/prezzi/{codart}/{idlist}")
    public double getPriceArt(@RequestHeader("Authorization") String AuthHeader, 
    		@PathVariable("codart") String CodArt, 
    		@PathVariable("idlist") String IdList);
	
	@GetMapping(value = "/api/prezzi/info/{codart}")
	public PrezzoDto getDefPriceArt2(@RequestHeader("Authorization") String AuthHeader, 
			@PathVariable("codart") String CodArt);
	
	@GetMapping(value = "/api/prezzi/info/{codart}/{idlist}")
    public PrezzoDto getPriceArt2(
    		@RequestHeader("Authorization") String AuthHeader, 
    		@PathVariable("codart") String CodArt, 
    		@PathVariable("idlist") String IdList);
	
	
	
}
