package com.semweb.dataIngestion;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class getData_stas_stop_times {
	String datasetURL = "http://localhost:3030/data"; //curl -L -H "Accept:text/turtle" \  localhost:3030/<dataset>/ > data.ttl
	String sparqlEndpoint = datasetURL + "/sparql";
	String sparqlUpdate = datasetURL + "/update";
	String graphStore = datasetURL + "/data";
	public void getStops() {

		String csvFile = "src/main/resources/stas/stop_times.txt";
		String ExC = "http://www.example.org/classes/#";
		String ExP = "http://www.example.org/properties/#";
		String Trips= "http://www.example.org/trips/#";
		String Stops=  "http://www.example.org/stops/#";
		String StopPoints= "http://www.example.org/stoppoints/#";

		Model model = ModelFactory.createDefaultModel();


		Property RDFType = model.createProperty(RDF.type.getURI());
		//Property RDFSSubclass = model.createProperty(RDFS.subClassOf.getURI());
		Property RDFSDomain = model.createProperty(RDFS.domain.getURI());
		Property RDFSRange =model.createProperty(RDFS.range.getURI());
		//Property RDFSLabel = model.createProperty(RDFS.label.getURI());

		//model.createTypedLiteral("07:01:00", (RDFDatatype) XSD.time);

		//org.apache.jena.vocabulary.
		//model.setNsPrefix("geo","http://www.w3.org/2003/01/geo/wgs84_pos#");
		//model.getNsPrefixURI("geo");


		Property TripHasStop = model.createProperty(ExP + "TripHasStop");
		Property StopHasTrip = model.createProperty(ExP + "StopHasTrip");
		Property StopHasStopPoint = model.createProperty(ExP + "StopHasStopPoint");
		Property StopHasArrivalTime  = model.createProperty(ExP + "StopHasArrivalTime");
		Property StopHasDeparatureTime = model.createProperty(ExP + "StopHasDeparatureTime");
		Property StopHasStopSequence = model.createProperty(ExP + "StopHasStopSequence");

		Resource TripClass = model.createResource(ExC+"Trip");

		Resource StopPointClass = model.createResource(ExC+"StopPoint");

		Resource StopClass = model.createResource(ExC+"Stop");


		//Resource ServiceClass = model.createResource(ExC+"Service");


		TripHasStop.addProperty(RDFSDomain,TripClass);
		TripHasStop.addProperty(RDFSRange,StopClass);

		StopHasTrip.addProperty(RDFSDomain,StopClass);
		StopHasTrip.addProperty(RDFSRange,TripClass);

		StopHasStopPoint.addProperty(RDFSDomain,StopClass);
		StopHasStopPoint.addProperty(RDFSRange,StopPointClass);

		StopHasArrivalTime.addProperty(RDFSDomain,StopClass);

		StopHasDeparatureTime.addProperty(RDFSDomain,StopClass);

		StopHasStopSequence.addProperty(RDFSDomain,StopClass);



		CSVReader reader = null;

		int i = 0;
		int nbligne=0;


		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);

		try {

			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			line = reader.readNext();

			while ((line = reader.readNext()) != null) {
				nbligne++;
				String Id = "STAS"+line[0];

				Resource trip = model.createResource(Trips+Id);
				trip.addProperty(RDFType, TripClass);

				String[] heurearrivee = line[1].split(":");
				String stop_id = Id+"-"+heurearrivee[0]+heurearrivee[1]+"-seq"+line[4];

				System.out.println(line[0]+"  "+stop_id);

				Resource stop = model.createResource(Stops+stop_id);
				stop.addProperty(RDFType,StopClass);

				trip.addProperty(TripHasStop,stop);
				stop.addProperty(StopHasTrip,trip);

				Literal arrival_time = model.createTypedLiteral(line[1],XSD.time.getURI());
				Literal deparature_time = model.createTypedLiteral(line[2],XSD.time.getURI());




				stop.addProperty(StopHasArrivalTime,arrival_time);
				stop.addProperty(StopHasDeparatureTime,deparature_time);

				stop.addProperty(StopHasStopSequence,line[4]);

				String stoppoint_id="STAS"+line[3];

				if (stoppoint_id != null) {
					Resource stoppoint = model.createResource(StopPoints + stoppoint_id);
					stop.addProperty(StopHasStopPoint, stoppoint);
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
			System.out.println("nombre de lignes: " + nbligne);
			System.out.println(reader.getLinesRead());
			System.out.println(reader.getSkipLines());


		}

	}


	public static void main(String[] args) {
		getData_stas_stop_times GetIt = new getData_stas_stop_times();
		GetIt.getStops();
	}

}

