package org.example;

import org.apache.jena.ontapi.model.OntClass;
import org.apache.jena.ontapi.model.OntModel;
import org.example.data.RequestTargets;
import org.example.data.TriasRequests;
import org.example.ontology.OntologyExample;
import org.implementation.Coord;
import org.implementation.HelperFunctions;

import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        //JohnSmith johnSmithClass = new JohnSmith();
        // RDFTutorial.printModel(johnSmithClass.model);
        // Model model = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-1.rdf");
        // Model model1 = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-2.rdf");
        // Model model2 = RDFReader.readRDFFile("/Users/leonprivat/Documents/GitHub/bachelorthesis_project/SemanticModelPrototype/src/main/java/org/example/vc-db-3.rdf");
        // RDFTutorial.containers(model);

        //Ontology:
        // OntModel m = OntologyExample.loadModelFromString(HelperFunctions.readFiletoString("protege/public_transit_ontology.rdf", true));
        OntModel om = OntologyExample.loadModelFromFile("protege/public_transit_ontology.rdf");
        // example operations on model:
        Stream<OntClass.Named> classes = om.classes();
        List<OntClass.Named> classList = classes.toList();
        System.out.println("Classes: " + classList);

        //String res = TriasRequests.locInfoReq("Strasbourg", RequestTargets.VRN_TRIAS);
        // String res = TriasRequests.coordToCoordTripReq(new Coord(8.38755106233198f, 49.01261399475719f), new Coord(8.41064385412417f, 49.00505228988602f), HelperFunctions.getCurrentTime (), (short)1, RequestTargets.VRN_TRIAS);
        String res = TriasRequests.stationToStationTripRequest("de:08317:14506", "fr:24067:58", HelperFunctions.getCurrentTime(), (short)1, RequestTargets.VRN_TRIAS);
        System.out.println("Response Body:\n " + res);
    }
}