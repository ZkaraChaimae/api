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

public class stas_stops {
	String datasetURL = "http://localhost:3030/data"; //curl -L -H "Accept:text/turtle" \  localhost:3030/<dataset>/ > data.ttl
	String sparqlEndpoint = datasetURL + "/sparql";
	String sparqlUpdate = datasetURL + "/update";
	String graphStore = datasetURL + "/data";
	public void getStops() {

		String csvFile = "src/main/resources/stas/stops.txt";
		String ExC ="http://www.example.org/classes/#";
		String ExP = "http://www.example.org/properties/#";
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


		Property Lat = model.createProperty(geo + "lat");
		Property Lon = model.createProperty(geo + "long");

		Resource GeoPoint = model.createResource(geo + "Point");

		Resource StopPointClass = model.createResource(ExC+"StopPoint");
		StopPointClass.addProperty(RDFSSubclass,GeoPoint);


		CSVReader reader = null;
		int i = 0;
		int nbligne=0;

		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);

		try {

			reader = new CSVReader(new FileReader(csvFile));
			String[] line;

			while ((line = reader.readNext()) != null) {
				nbligne++;
				String Id = "STAS" + line[0];
				Resource stoppoint = model.createResource(StopPoints+Id);
				stoppoint.addProperty(RDFType, StopPointClass);
				stoppoint.addProperty(RDFSLabel,line[1]);
				stoppoint.addProperty(Lat, line[2]);
				stoppoint.addProperty(Lon, line[3]);


				//writing the Model variable "model" to the  fuseki triple store in the
				// dataset "stops"
				i++;
				if (i == 100) {
					// Writing the rdf :
					//model.write(System.out, "RDF/XML");
					conneg.load(model);
					model.removeAll();
					i = 0;
				}
				//j++;
			}
			conneg.load(model);
			model.removeAll();
		}

		catch (IOException e) {e.printStackTrace();}
		//catch (java.io.FileNotFoundException fe) { fe.printStackTrace(); }
		//catch(CsvValidationException ve) { ve.printStackTrace();}



		finally {

			System.out.println("nombre de lignes: " + nbligne);
			System.out.println(reader.getLinesRead());


		}

	}


	public static void main(String[] args) {
		stas_stops GetIt = new stas_stops();
		GetIt.getStops();
	}

}

