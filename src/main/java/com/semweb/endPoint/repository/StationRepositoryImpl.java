package com.semweb.endPoint.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.semweb.dataIngestion.getData;
import com.semweb.endPoint.entities.GeoPoint;
import com.semweb.endPoint.entities.Station;

//public class stationRepository implements JpaRepository<station, String> {
public class StationRepositoryImpl implements StationRepository{
	private String datasetURL = "http://localhost:3030/stops";
	private String sparqlEndpoint = datasetURL + "/sparql";
	private String sparqlUpdate = datasetURL + "/update";
	private String graphStore = datasetURL + "/data";
	
	

	public static void main(String[] args) {
		StationRepositoryImpl impl = new StationRepositoryImpl();
		List<Station> stas = impl.findAll();
		System.out.println("########################################");
		
		for(Station station: stas) {
			System.out.println(station.toString());
		}
		System.out.println(stas.size());
	}



	public List<Station> findAll() {
		List<Station> stations = new ArrayList<Station>();
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint, sparqlUpdate, graphStore);
		QueryExecution qExec = conn.query("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+"PREFIX geoloc: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+"SELECT DISTINCT ?s ?label ?geopoint ?lat ?long ?arret ?typeArret"
						+"WHERE {"
						+"?s  a <http://www.example.org/classes/#StopArea> ;"
						+"  rdfs:label ?label ;"
						+" <http://www.example.org/properties/#HasGeoPoint> ?geopoint ;"
						+" <http://www.example.org/properties/#HasStopPoint> ?arret."
						+"  ?arret a ?typeArret ."
						+"  ?geopoint a <http://www.w3.org/2003/01/geo/wgs84_pos#Point> ;"
						 +"  <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;"
						   +"<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long ."
						+"}"
						//+"limit 1000"
						);
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			stations.add(new Station(qs.getResource("s").getLocalName(), qs.getLiteral("label").toString(), new GeoPoint(qs.getLiteral("lat").toString(), qs.getLiteral("long").toString()), "station"));
		}
		qExec.close();conn.close();
		return stations;
	}
	
	public List<Station> findAllTrains() {
		List<Station> stations = new ArrayList<Station>();
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint, sparqlUpdate, graphStore);
		QueryExecution qExec = conn.query("prefix classes: <http://www.example.org/classes/#>"
				+"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"PREFIX geo:<https://www.w3.org/2003/01/geo/wgs84_pos#>"
				+"PREFIX properties: <http://www.example.org/properties/#>"
				+"SELECT   ?stop  ?stop_label ?stop_latitude ?stop_longitude ?station ?station_label\r\n"
				+ "WHERE { \r\n"
				+ "?stop  a classes:OCETrain ;\r\n"
				+ "  rdfs:label ?stop_label ;\r\n"
				+ "   geo:lat ?stop_latitude;\r\n"
				+ "   geo:long ?stop_longitude ;\r\n"
				+ "   properties:BelongsToStation ?station .\r\n"
				+ "   ?station rdfs:label ?station_label .\r\n"
				+ "}\r\n"
				+ "limit 1000 ");
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			stations.add(new Station(qs.getResource("stop").getLocalName(), qs.getLiteral("stop_label").toString(), new GeoPoint(qs.getLiteral("stop_latitude").toString(), qs.getLiteral("stop_longitude").toString()), "train"));
		}
		qExec.close();conn.close();
		return stations;
	}
	
	public List<Station> findAllTrams() {
		List<Station> stations = new ArrayList<Station>();
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint, sparqlUpdate, graphStore);
		QueryExecution qExec = conn.query("prefix classes: <http://www.example.org/classes/#>"
				+"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"PREFIX geo:<https://www.w3.org/2003/01/geo/wgs84_pos#>"
				+"PREFIX properties: <http://www.example.org/properties/#>"
				+"SELECT   ?stop  ?stop_label ?stop_latitude ?stop_longitude ?station ?station_label\r\n"
				+ "WHERE { \r\n"
				+ "?stop  a classes:OCETramtrain ;\r\n"
				+ "  rdfs:label ?stop_label ;\r\n"
				+ "   geo:lat ?stop_latitude;\r\n"
				+ "   geo:long ?stop_longitude ;\r\n"
				+ "   properties:BelongsToStation ?station .\r\n"
				+ "   ?station rdfs:label ?station_label .\r\n"
				+ "}\r\n"
				+ "limit 1000 ");
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			stations.add(new Station(qs.getResource("stop").getLocalName(), qs.getLiteral("stop_label").toString(), new GeoPoint(qs.getLiteral("stop_latitude").toString(), qs.getLiteral("stop_longitude").toString()), "tram"));
		}
		qExec.close();conn.close();
		return stations;
	}
	
	public List<Station> findAllBuses() {
		List<Station> stations = new ArrayList<Station>();
		RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpoint, sparqlUpdate, graphStore);
		QueryExecution qExec = conn.query("prefix classes: <http://www.example.org/classes/#>"
				+"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"PREFIX geo:<https://www.w3.org/2003/01/geo/wgs84_pos#>"
				+"PREFIX properties: <http://www.example.org/properties/#>"
				+"SELECT   ?stop  ?stop_label ?stop_latitude ?stop_longitude ?station ?station_label\r\n"
				+ "WHERE { \r\n"
				+ "?stop  a classes:OCECar ;\r\n"
				+ "  rdfs:label ?stop_label ;\r\n"
				+ "   geo:lat ?stop_latitude;\r\n"
				+ "   geo:long ?stop_longitude ;\r\n"
				+ "   properties:BelongsToStation ?station .\r\n"
				+ "   ?station rdfs:label ?station_label .\r\n"
				+ "}\r\n"
				+ "limit 1000 ");
		ResultSet rs = qExec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			stations.add(new Station(qs.getResource("stop").getLocalName(), qs.getLiteral("stop_label").toString(), new GeoPoint(qs.getLiteral("stop_latitude").toString(), qs.getLiteral("stop_longitude").toString()), "bus"));
		}
		qExec.close();conn.close();
		return stations;
	}



	public List<Station> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}



	public List<Station> findAllById(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}



	public void flush() {
		// TODO Auto-generated method stub
		
	}



	public <S extends Station> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}



	public void deleteInBatch(Iterable<Station> entities) {
		// TODO Auto-generated method stub
		
	}



	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}



	public Station getOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}



	public Page<Station> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}



	public Optional<Station> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean existsById(String id) {
		// TODO Auto-generated method stub
		return false;
	}



	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}



	public void deleteById(String id) {
		// TODO Auto-generated method stub
		
	}



	public void delete(Station entity) {
		// TODO Auto-generated method stub
		
	}



	public void deleteAll(Iterable<? extends Station> entities) {
		// TODO Auto-generated method stub
		
	}



	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}



	public <S extends Station> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}



	public <S extends Station> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}



	public <S extends Station> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}


}
