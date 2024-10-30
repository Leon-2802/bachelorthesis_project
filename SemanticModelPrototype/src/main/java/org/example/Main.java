package org.example;

import org.example.data.VRNRequests;
import org.implementation.Coord;

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
        String res = VRNRequests.vrnCoordToCoordTripReq(new Coord(7.77346f, 48.58808f), new Coord(7.94174f, 48.45923f), "1");
        System.out.println("Response: " + res);
    }
}