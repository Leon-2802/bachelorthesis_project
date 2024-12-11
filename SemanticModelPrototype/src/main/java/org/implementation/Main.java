package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
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
            Map<String, Map<String, String>> tripsData = GTFSLoader.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/trips.txt", "trip_id");
            Map<String, Map<String, String>> routesData = GTFSLoader.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/routes.txt", "route_id");
            Map<String, Map<String, String>> stopsData = GTFSLoader.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/stops.txt", "stop_id");
            List<Map<String, String>> transferData = GTFSLoader.parseGTFSFileToList("data/fluo-grand-est-fluo67-gtfs/transfers.txt", "to_stop_id");
            List<Map<String, String>> stopTimesData = GTFSLoader.parseGTFSFileToList("data/fluo-grand-est-fluo67-gtfs/stop_times.txt", "trip_id");
            // Print the parsed data
            System.out.println("Parsed stop times data:");
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
            Document testDoc = GTFSLoader.loadTripToXML("10", tripsData, stopTimesData, stopsData);
            String testString = HelperFunctions.documentToString(testDoc);
            System.out.println("Test XML Document:\n" + testString);
        }
        catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
        catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException: " + ex.getMessage());
        } catch (Exception ex) {
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