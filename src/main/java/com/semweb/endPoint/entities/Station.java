package com.semweb.endPoint.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Station {
	private @Id String stationID;
	private String label;
	private GeoPoint location;
	private String type;
	
	public Station() {
		
	}

	public Station(String stationID, String label, GeoPoint location, String type) {
		this.stationID = stationID;
		this.label = label;
		this.location = location;
		this.type = type;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public GeoPoint getLocation() {
		return location;
	}
	
	public void setType(String t) {
		this.type = t;
	}
	
	public String getType() {
		return this.type;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}
	
	
	@Override
	public String toString() {
		return this.getStationID() + " " + this.getLabel() + " " + this.getLocation().getLat() + " " + this.getLocation().getLon() + " " + this.getType();
	}
}
