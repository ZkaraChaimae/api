package com.semweb.dataIngestion;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class getData {
	String datasetURL = "http://localhost:3030/data"; //curl -L -H "Accept:text/turtle" \  localhost:3030/<dataset>/ > data.ttl
	String sparqlEndpoint = datasetURL + "/sparql";
	String sparqlUpdate = datasetURL + "/update";
	String graphStore = datasetURL + "/data";
	public void getStops() {

		String csvFile = "src/main/resources/sncf/stops.txt";
		String ExC ="http://www.example.org/classes/#";
		String ExP = "http://www.example.org/properties/#";
		String StopAreas= "http://www.example.org/stopareas/#";
		String StopPoints= "http://www.example.org/stoppoints/#";

		String geo = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		Model model = ModelFactory.createDefaultModel();


		Property RDFType = model.createProperty(RDF.type.getURI());
		Property RDFSSubclass = model.createProperty(RDFS.subClassOf.getURI());
		Property RDFSDomain = model.createProperty(RDFS.domain.getURI());
		Property RDFSRange =model.createProperty(RDFS.range.getURI());
		Property RDFSLabel = model.createProperty(RDFS.label.getURI());

		//org.apache.jena.vocabulary.
		//model.setNsPrefix("geo","http://www.w3.org/2003/01/geo/wgs84_pos#");
		//model.getNsPrefixURI("geo");


		Property HasStopPoint = model.createProperty(ExP + "HasStopPoint");
		Property BelongsToStation = model.createProperty(ExP + "BelongsToStation");
		Property HasGeoPoint = model.createProperty(ExP + "HasGeoPoint");

		Property Lat = model.createProperty(geo + "lat");
		Property Lon = model.createProperty(geo + "long");
		Resource StopAreaClass = model.createResource(ExC+"StopArea");

		Resource GeoPoint = model.createResource(geo + "Point");
		Resource StopPointClass = model.createResource(ExC+"StopPoint");
		Resource OCETrainClass = model.createResource(ExC+"OCETrain");
		Resource OCETramtrainClass = model.createResource(ExC+"OCETramtrain");
		Resource OCECarClass = model.createResource(ExC+"OCECar");

		HasStopPoint.addProperty(RDFSDomain,StopAreaClass);
		HasStopPoint.addProperty(RDFSRange,StopPointClass);

		BelongsToStation.addProperty(RDFSDomain,StopPointClass);
		BelongsToStation.addProperty(RDFSRange,StopAreaClass);

		HasGeoPoint.addProperty(RDFSDomain,StopPointClass);
		HasGeoPoint.addProperty(RDFSDomain,StopAreaClass);
		HasGeoPoint.addProperty(RDFSRange,GeoPoint);

		StopPointClass.addProperty(RDFSSubclass,GeoPoint);

		OCETrainClass.addProperty(RDFSSubclass,StopPointClass);
		OCETramtrainClass.addProperty(RDFSSubclass,StopPointClass);
		OCECarClass.addProperty(RDFSSubclass,StopPointClass);

		CSVReader reader = null;


		int i = 0;
		int nbligne=0;
		int nbareas=0;
		int nbtrain=0;
		int nbcar=0;
		int nbtram=0;

		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);



		try {

			reader = new CSVReader(new FileReader(csvFile));
			String[] line;

			while ((line = reader.readNext()) != null) {
				nbligne++;
				if ((line[0].startsWith("StopArea:")) && (line[0].length()>9)) {
						String Id = line[0].substring(9);
						Resource stoparea = model.createResource(StopAreas+Id);
						stoparea.addProperty(RDFType, StopAreaClass);
						Resource geopoint = model.createResource("Lat:"+line[3]+",Lon:"+line[4]);
						geopoint.addProperty(RDFType,GeoPoint);
						geopoint.addProperty(Lat,line[3]);
						geopoint.addProperty(Lon,line[4]);
						stoparea.addProperty(HasGeoPoint,geopoint);
						stoparea.addProperty(RDFSLabel,line[1]);
						nbareas++;
				}
				else {
					if ((line[0].startsWith("StopPoint:OCETrain")) && (line[0].length() > 19)) {                    //OCETrain
						String Id = line[0].substring(19);
						Resource trainstation = model.createResource(StopPoints + Id);
						trainstation.addProperty(RDFType, StopPointClass);
						trainstation.addProperty(RDFType, OCETrainClass);
						trainstation.addProperty(Lat, line[3]);
						trainstation.addProperty(Lon, line[4]);
						String parentstation = ((line[8]).split(":"))[1];
						Resource ParentStation = model.createResource(StopAreas + parentstation);
						trainstation.addProperty(BelongsToStation, ParentStation);
						ParentStation.addProperty(HasStopPoint, trainstation);
						trainstation.addProperty(RDFSLabel, line[1]);
						nbtrain++;
					} else {
						if ((line[0].startsWith("StopPoint:OCECar")) && (line[0].length() > 17)) {   //OCECar
							//System.out.println(line[0]);
							String Id = line[0].substring(17);
							Resource busstation = model.createResource(StopPoints + Id);
							busstation.addProperty(RDFType, StopPointClass);
							busstation.addProperty(RDFType, OCECarClass);
							busstation.addProperty(Lat, line[3]);
							busstation.addProperty(Lon, line[4]);
							String parentstation = ((line[8]).split(":"))[1];
							Resource ParentStation = model.createResource(StopAreas + parentstation);
							busstation.addProperty(BelongsToStation, ParentStation);
							ParentStation.addProperty(HasStopPoint, busstation);
							busstation.addProperty(RDFSLabel, line[1]);
							nbcar++;
						} else {
							if ((line[0].startsWith("StopPoint:OCETramtrain")) && (line[0].length() > 23)) {        //OCETramTrain
								String Id = line[0].substring(23);
								Resource tramstation = model.createResource(StopPoints + Id);
								tramstation.addProperty(RDFType, StopPointClass);
								tramstation.addProperty(RDFType, OCETramtrainClass);
								tramstation.addProperty(Lat, line[3]);
								tramstation.addProperty(Lon, line[4]);
								String parentstation = ((line[8]).split(":"))[1];
								Resource ParentStation = model.createResource(StopAreas + parentstation);
								tramstation.addProperty(BelongsToStation, ParentStation);
								ParentStation.addProperty(HasStopPoint, tramstation);
								tramstation.addProperty(RDFSLabel, line[1]);
								nbtram++;
							}
							else {
								System.out.println(line[0]);
							}
						}
					}
				}
				//writing the Model variable "model" to the  fuseki triple store in the
				// dataset "stops"
				i++;
				if(i == 100) {
					// Writing the rdf :
					//model.write(System.out, "RDF/XML");
					conneg.load(model);
					model.removeAll();
					i=0;
				}
				//j++;
			}
			conneg.load(model);
			model.removeAll();
		}
		catch (IOException e) {e.printStackTrace();}
		finally {
			System.out.println("All finished - getStops");
			System.out.println("StopAreas: " + nbareas);
			System.out.println("TrainStaions: " + nbtrain);
			System.out.println("CarStations: " + nbcar);
			System.out.println("Tramstations: " + nbtram);
			System.out.println("nombre de lignes: " + nbligne);
			System.out.println(reader.getLinesRead());
		}
	}

	public static void main(String[] args) {
		getData GetIt = new getData();
		GetIt.getStops();
		
		getData_sncf_stop_times data_sncf_stop_times = new getData_sncf_stop_times();
		data_sncf_stop_times.getStops();
		
		getData_sncf_trips data_sncf_trips = new getData_sncf_trips();
		data_sncf_trips.getStops();
		
		getData_stas_routes data_stas_routes = new getData_stas_routes ();
		data_stas_routes.getStops();
		
		getData_stas_stop_times data_stas_stop_times = new getData_stas_stop_times();
		data_stas_stop_times.getStops();
		
		getData_stas_trips data_stas_trips = new getData_stas_trips();
		data_stas_trips.getStops();
		
		stas_stops stas_stops = new stas_stops();
		stas_stops.getStops();
	}

}

