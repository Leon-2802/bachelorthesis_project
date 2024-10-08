package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class Main {
    public static void main(String[] args) {
        JohnSmith johnSmithClass = new JohnSmith();
        // RDFTutorial.printModel(johnSmithClass.model);
        Model model = RDFReader.readRDFFile("C:\\Users\\Le_go\\Documents\\GitHub\\bachelorthesis_project\\SemanticModelPrototype\\src\\main\\java\\org\\example\\vc-db-1.rdf");
        Model model1 = RDFReader.readRDFFile("C:\\Users\\Le_go\\Documents\\GitHub\\bachelorthesis_project\\SemanticModelPrototype\\src\\main\\java\\org\\example\\vc-db-2.rdf");
        Model model2 = RDFReader.readRDFFile("C:\\Users\\Le_go\\Documents\\GitHub\\bachelorthesis_project\\SemanticModelPrototype\\src\\main\\java\\org\\example\\vc-db-3.rdf");
        RDFTutorial.containers(model);
    }
}