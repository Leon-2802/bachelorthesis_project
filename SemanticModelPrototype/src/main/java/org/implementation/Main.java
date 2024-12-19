package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.shared.NotFoundException;
import org.implementation.gtfs.GTFSLoader;
import org.implementation.gtfs.GTFSOntologyService;
import org.implementation.trias.TriasRequests;
import org.implementation.trias.RequestTargets;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//         load the OWL public transit ontology from file and intitialize the Ontology Service class:
        OntModel om = HelperFunctions.loadModelFromFile("protege/public_transit_ontology.rdf");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'TRIAS' to run the TRIAS-example, or 'GTFS' for the GTFS-example");
        String runChoice = scanner.nextLine();
        if (runChoice.equalsIgnoreCase("TRIAS")) {
            // Example TRIAS-IDs:
            // de:08222:2453, de:08221:1199
            System.out.println("Enter the TRIAS-ID of the origin station");
            String originStationId = scanner.nextLine();
            System.out.println("Enter the TRIAS-ID of the destination station");
            String destinationStationId = scanner.nextLine();
            System.out.println("Do you want to print the XML-Response from the VRN TRIAS API? (Y/N)");
            String printXMLChoice = scanner.nextLine();
            boolean printXML = printXMLChoice.equalsIgnoreCase("Y");
            runTriasExample(om, originStationId, destinationStationId, printXML);
        }
        else if (runChoice.equalsIgnoreCase("GTFS")) {
            // Example GTFS-IDs:
            // 566A -157A
            // 105R - 114R
            System.out.println("Enter the GTFS-ID of the origin station");
            String originStationId = scanner.nextLine();
            System.out.println("Enter the GTFS-ID of the destination station");
            String destinationStationId = scanner.nextLine();
            System.out.println("Do you want to print the XML-File of the parsed GTFS-Data? (Y/N)");
            String printXMLChoice = scanner.nextLine();
            boolean printXML = printXMLChoice.equalsIgnoreCase("Y");
            runGTFSExample(om,originStationId, destinationStationId, printXML);
        }
        else {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        SparQLService.sendQuery(om);
    }

    private static void runGTFSExample(OntModel om, String originId, String destinationId, boolean printXML) {
        // load the GTFS data from file and create XML trip data:
        try {
            GTFSLoader gtfsLoader = new GTFSLoader();
            GTFSOntologyService gtfsOntService = new GTFSOntologyService(om);

            Document test = gtfsLoader.extractConnectionDataToXML(originId, destinationId);
            gtfsOntService.mapGtfsToOntology(test);
            if (printXML) {
                String testString = HelperFunctions.documentToString(test);
                System.out.println("Trip XML Document:\n" + testString);
            }
            System.out.println("SPARQL results for GTFS trip from stop " + originId + " to " + destinationId + " mapped to ontology in area of agency Fluo Grand Est 67:");
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

    private static void runTriasExample(OntModel om, String originStation, String destinationStation, boolean printXML) {
        // create the Ontology Service instance for the Ontology Model:
        TriasOntologyService triasOntService = new TriasOntologyService(om);

        // send the trip request from Planetarium, Mannheim to Volkshochschule, Heidelberg
        TriasRequests tr = new TriasRequests();

        String res = tr.stationToStationTripRequest(originStation, destinationStation, HelperFunctions.getCurrentTime(), (short)1, true, RequestTargets.VRN_TRIAS);
        if (printXML) {
            System.out.println("TRIAS Response Body:\n " + res);
        }

        try {
            Document resDoc = HelperFunctions.stringToDocument(res, true);
            triasOntService.mapTriasResToOntology(resDoc);
            System.out.println("SPARQL results for TRIAS trip mapped to ontology from stop with id " + originStation + " to " + destinationStation + " in area of agency VRN:");
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