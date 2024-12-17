package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.shared.NotFoundException;
import org.implementation.gtfs.GTFSLoader;
import org.implementation.trias.TriasRequests;
import org.implementation.trias.RequestTargets;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//         load the OWL public transit ontology from file and intitialize the Ontology Service class:
//        OntModel om = HelperFunctions.loadModelFromFile("protege/public_transit_ontology.rdf");
//        runTriasExample(om);
        runGTFSExample();
//        SparQLService.sendQuery(om);
    }

    private static void runGTFSExample() {
        // load the GTFS data from file and create XML trip data:
        try {
            GTFSLoader gtfsLoader = new GTFSLoader();
            // Print the parsed data
//            System.out.println("Parsed stop times data:");
//            for (Map.Entry<String, Map<String, String>> entry : routesData.entrySet()) {
//                System.out.println("Key: " + entry.getKey());
//                for (Map.Entry<String, String> field : entry.getValue().entrySet()) {
//                    System.out.println("  " + field.getKey() + ": " + field.getValue());
//                }
//            }
//            for (Map<String, String> dataEntry : stopTimesData) {
//                System.out.println("Map:");
//                for (Map.Entry<String, String> entry : dataEntry.entrySet()) {
//                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
//                }
//            }
//            Document trip01 = gtfsLoader.loadTripToXML("3524499");
//            Document trip02 = gtfsLoader.loadTripToXML("3524499");
            // ! restructure the findconnection function, so it takes two trips (1st trip from start, second trip until end) and adds transfer in between
//            gtfsLoader.findConnection("254R", "5696A");
            Document test = gtfsLoader.extractConnectionDataToXML("566A", "157A");
            // 566A -157A
            // 105R - 114R
            String testString = HelperFunctions.documentToString(test);
            System.out.println("Trip XML Document:\n" + testString);
        }
        catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException: " + ex.getMessage());
        } catch (NotFoundException ex) {
            System.out.println("NotFoundException: " + ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println("Unexpected Exception occurred: " + ex.getMessage());
        }
    }

    private static void runTriasExample(OntModel om) {
        // create the Ontology Service instance for the Ontology Model:
        TriasOntologyService triasOntService = new TriasOntologyService(om);

        // send the trip request from Planetarium, Mannheim to Volkshochschule, Heidelberg
        TriasRequests tr = new TriasRequests();
        String res = tr.stationToStationTripRequest("de:08222:2453", "de:08221:1199", HelperFunctions.getCurrentTime(), (short)1, true, RequestTargets.VRN_TRIAS);
        System.out.println("Response Body:\n " + res);

        try {
            Document resDoc = HelperFunctions.stringToDocument(res, true);
            triasOntService.mapTriasResToOntology(resDoc);
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