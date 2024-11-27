package org.example;

import org.apache.jena.ontapi.model.OntClass;
import org.apache.jena.ontapi.model.OntModel;
import org.implementation.trias.request_templates.RequestTargets;
import org.implementation.trias.TriasRequests;
import org.example.ontology.OntologyExample;
import org.implementation.HelperFunctions;

import java.util.List;
import java.util.stream.Stream;

public final class ExampleRunner {
    private ExampleRunner() {}

    public static void Run() {
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
    }
}
