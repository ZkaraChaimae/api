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

public class getData_stas_routes {
	String datasetURL = "http://localhost:3030/data"; //curl -L -H "Accept:text/turtle" \  localhost:3030/<dataset>/ > data.ttl
	String sparqlEndpoint = datasetURL + "/sparql";
	String sparqlUpdate = datasetURL + "/update";
	String graphStore = datasetURL + "/data";
	public void getStops() {

		String csvFile = "src/main/resources/stas/routes.txt";
		String ExC = "http://www.example.org/classes/#";
		String ExP = "http://www.example.org/properties/#";
		String Routes=  "http://www.example.org/routes/#";
		String Agencies = "http://www.example.org/agencies/#";
		String RouteTypes = "http://www.example.org/routetypes/#";

		Model model = ModelFactory.createDefaultModel();


		Property RDFType = model.createProperty(RDF.type.getURI());
		Property RDFSSubclass = model.createProperty(RDFS.subClassOf.getURI());
		Property RDFSDomain = model.createProperty(RDFS.domain.getURI());
		Property RDFSRange =model.createProperty(RDFS.range.getURI());
		Property RDFSLabel = model.createProperty(RDFS.label.getURI());

		//org.apache.jena.vocabulary.
		//model.setNsPrefix("geo","http://www.w3.org/2003/01/geo/wgs84_pos#");
		//model.getNsPrefixURI("geo");


		Property HasRouteType = model.createProperty(ExP + "HasRouteType");

		Property RouteHasAgency = model.createProperty(ExP + "RouteHasAgency");
		Property AgencyHasRoute = model.createProperty(ExP + "AgencyHasRoute");

		Property RouteHasShortName = model.createProperty(ExP + "RouteHasShortName");
		Property RouteHasColor = model.createProperty(ExP + "RouteHasColor");



		Resource RouteClass = model.createResource(ExC+"Route");
		Resource AgencyClass = model.createResource(ExC+"Agency");




		HasRouteType.addProperty(RDFSDomain,RouteClass);


		RouteHasAgency.addProperty(RDFSDomain,RouteClass);
		RouteHasAgency.addProperty(RDFSRange,AgencyClass);

		AgencyHasRoute.addProperty(RDFSDomain,AgencyClass);
		AgencyHasRoute.addProperty(RDFSRange,RouteClass);

		RouteHasShortName.addProperty(RDFSDomain,RouteClass);
		RouteHasColor.addProperty(RDFSDomain,RouteClass);


		Resource routetype_tram =model.createResource(RouteTypes+"0");
		Resource routetype_metro =model.createResource(RouteTypes+"1");
		Resource routetype_train =model.createResource(RouteTypes+"2");
		Resource routetype_bus =model.createResource(RouteTypes+"3");

		routetype_tram.addProperty(RDFSLabel,"Tram");
		routetype_metro.addProperty(RDFSLabel,"Metro");
		routetype_train.addProperty(RDFSLabel,"Train");
		routetype_bus.addProperty(RDFSLabel,"Bus");

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
				System.out.println(Id);
				Resource route = model.createResource(Routes+Id);
				route.addProperty(RDFType, RouteClass);

				Resource agency = model.createResource(Agencies+"STAS");

				route.addProperty(RouteHasAgency,agency);
				agency.addProperty(AgencyHasRoute,route);

				route.addProperty(RouteHasShortName,line[2]);
				route.addProperty(RDFSLabel,line[3]);

				if (line[4]=="3"){
					route.addProperty(HasRouteType,routetype_bus);
				}
				if (line[4]=="2"){
					route.addProperty(HasRouteType,routetype_train);
				}
				if (line[4]=="1"){
					route.addProperty(HasRouteType,routetype_metro);
				}
				if (line[4]=="0"){
					route.addProperty(HasRouteType,routetype_tram);
				}

				route.addProperty(RouteHasColor,line[5]);

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
		//catch (java.io.FileNotFoundException fe) { fe.printStackTrace(); }
		//catch(CsvValidationException ve) { ve.printStackTrace();}



		finally {
			System.out.println("All finished - getStops");
			System.out.println("nombre de lignes: " + nbligne);
			System.out.println(reader.getLinesRead());
			System.out.println(reader.getSkipLines());


		}

	}


	public static void main(String[] args) {
		getData_stas_routes  GetIt = new getData_stas_routes ();
		GetIt.getStops();
	}

}

