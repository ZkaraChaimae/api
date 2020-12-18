package com.semweb.endPoint.entities;

public class GeoPoint {
	private double lat;
	private double lon;

	public GeoPoint() {
	}

	public GeoPoint(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public GeoPoint(String lat, String lon) {
		this.lat = Double.parseDouble(lat);
		this.lon = Double.parseDouble(lon);
	}

	public final double lat() {
		return this.lat;
	}

	public final double getLat() {
		return this.lat;
	}

	public final double lon() {
		return this.lon;
	}

	public final double getLon() {
		return this.lon;
	}

}
