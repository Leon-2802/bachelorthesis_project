package org.example;

import org.example.data.RequestTargets;
import org.example.data.TriasRequests;
import org.implementation.Coord;
import org.implementation.HelperFunctions;

public class Main {
    public static void main(String[] args) {
        //JohnSmith johnSmithClass = new JohnSmith();
        // RDFTutorial.printModel(johnSmithClass.model);
        // Model model = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-1.rdf");
        // Model model1 = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-2.rdf");
        // Model model2 = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-3.rdf");
        // RDFTutorial.containers(model);

        //Ontology:
        // OntModel m = OntologyExample.loadModelFromString();
        // m.write(System.out, "RDF/XML-ABBREV");

        // String res = VRNRequests.vrnLocInfoReq("Strasbourg");
        // String res = TriasRequests.coordToCoordTripReq(new Coord(8.38755106233198f, 49.01261399475719f), new Coord(8.41064385412417f, 49.00505228988602f), HelperFunctions.getCurrentTime (), (short)1, RequestTargets.VRN_TRIAS);
        String res = TriasRequests.stationToStationTripRequest("de:06431:256", "de:08221:1199", HelperFunctions.getCurrentTime(), (short)1, RequestTargets.KVV_TRIAS);
        System.out.println("Response Body:\n " + res);
    }
}