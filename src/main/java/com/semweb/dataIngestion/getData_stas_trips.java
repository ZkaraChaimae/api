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

public class getData_stas_trips {
	String datasetURL = "http://localhost:3030/data"; //curl -L -H "Accept:text/turtle" \  localhost:3030/<dataset>/ > data.ttl
	String sparqlEndpoint = datasetURL + "/sparql";
	String sparqlUpdate = datasetURL + "/update";
	String graphStore = datasetURL + "/data";
	public void getStops() {

		String csvFile = "src/main/resources/stas/trips.txt";
		String ExC = "http://www.example.org/classes/#";
		String ExP = "http://www.example.org/properties/#";
		String Routes=  "http://www.example.org/routes/#";
		String Trips= "http://www.example.org/trips/#";
		String Service= "http://www.example.org/service/#";


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


		Property RouteHasTrip = model.createProperty(ExP + "RouteHasTrip");
		Property TripHasRoute = model.createProperty(ExP + "TripHasRoute");
		Property TripHasService = model.createProperty(ExP + "TripHasService");
		Property ServiceHasTrip = model.createProperty(ExP + "ServiceHasTrip");
		Property TripHasHeadSign = model.createProperty(ExP + "TripHasHeadSign");
		Property TripHasDirection = model.createProperty(ExP + "TripHasDirection");

		Resource RouteClass = model.createResource(ExC+"Route");

		Resource TripClass = model.createResource(ExC+"Trip");

		Resource ServiceClass = model.createResource(ExC+"Service");


		RouteHasTrip.addProperty(RDFSDomain,RouteClass);
		RouteHasTrip.addProperty(RDFSRange,TripClass);

		TripHasRoute.addProperty(RDFSDomain,TripClass);
		TripHasRoute.addProperty(RDFSRange,RouteClass);

		TripHasService.addProperty(RDFSDomain,TripClass);
		TripHasService.addProperty(RDFSRange,ServiceClass);

		ServiceHasTrip.addProperty(RDFSDomain,ServiceClass);
		ServiceHasTrip.addProperty(RDFSRange,TripClass);

		TripHasHeadSign.addProperty(RDFSDomain,TripClass);
		TripHasDirection.addProperty(RDFSRange,TripClass);


		CSVReader reader = null;

		int i = 0;
		int nbligne=0;


		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);


		try {

			reader = new CSVReader(new FileReader(csvFile));

			String[] line;

			while ((line = reader.readNext()) != null) {
				nbligne++;
				String Id = "STAS"+line[2];
				System.out.println(nbligne+" : "+Id);
				Resource trip = model.createResource(Trips+Id);
				trip.addProperty(RDFType, TripClass);

				Resource route = model.createResource(Routes+"STAS"+line[0]);
				route.addProperty(RDFType,RouteClass);
				trip.addProperty(TripHasRoute,route);
				route.addProperty(RouteHasTrip,trip);

				Resource service = model.createResource(Service+line[1]);
				service.addProperty(RDFType,ServiceClass);
				trip.addProperty(TripHasService,service);
				service.addProperty(ServiceHasTrip,trip);

				trip.addProperty(TripHasHeadSign,line[3]);

				trip.addProperty(TripHasDirection,line[4]);
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
		getData_stas_trips GetIt = new getData_stas_trips();
		GetIt.getStops();
	}

}

