package com.semweb.endPoint.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semweb.endPoint.entities.Station;
import com.semweb.endPoint.repository.StationRepositoryImpl;

@RestController
@RequestMapping("/api")
public class StationController {
	
	private StationRepositoryImpl stationRepo = new StationRepositoryImpl();
	
	@CrossOrigin
	@GetMapping("/stations")
	public List<Station> getAllStations() {
		return stationRepo.findAll();
	}
	
	@CrossOrigin
	@GetMapping("/trains")
	public List<Station> getAllTrains() {
		return stationRepo.findAllTrains();
	}
	
	@CrossOrigin
	@GetMapping("/trams")
	public List<Station> getAllTrams() {
		return stationRepo.findAllTrams();
	}
	
	@CrossOrigin
	@GetMapping("/buses")
	public List<Station> getAllBuses() {
		return stationRepo.findAllBuses();
	}

}
