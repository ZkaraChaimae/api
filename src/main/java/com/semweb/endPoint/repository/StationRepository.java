package com.semweb.endPoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.semweb.endPoint.entities.Station;

public interface StationRepository extends JpaRepository<Station, String>{

}
