package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.implementation.trias.TriasRequests;
import org.implementation.trias.RequestTargets;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // load the OWL public transit ontology from file and intitialize the Ontology Service class:
        OntModel om = HelperFunctions.loadModelFromFile("protege/public_transit_ontology.rdf");
        // create the Ontology Service instance for the Ontology Model:
        TriasOntologyService triasOntService = new TriasOntologyService(om);

        // send the trip request from Planetarium, Mannheim to Volkshochschule, Heidelberg
        TriasRequests tr = new TriasRequests();
        String res = tr.stationToStationTripRequest("de:08222:2453", "de:08221:1199", HelperFunctions.getCurrentTime(), (short)1, true, RequestTargets.VRN_TRIAS);
        System.out.println("Response Body:\n " + res);

        try {
            Document resDoc = HelperFunctions.stringToDocument(res, true);
            triasOntService.mapTriasResToOntology(resDoc);
            // Test Query:
            SparQLService.sendQuery(om);
        }
        catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException: " + ex.getMessage());
        }
        catch (SAXException ex) {
            System.out.println("SAXException: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
        catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException: " + ex.getMessage());
        }
    }
}