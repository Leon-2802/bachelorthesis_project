package org.example;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class Main {
    public static void main(String[] args) {
        JohnSmith johnSmithClass = new JohnSmith();
        // print out smaller models in a pretty way:
        RDFDataMgr.write(System.out, johnSmithClass.model, Lang.RDFXML);
        // for larger models:
        RDFDataMgr.write(System.out, johnSmithClass.model, Lang.NTRIPLES);
        RDFReader.readRDFFile("C:\\Users\\Le_go\\Documents\\GitHub\\bachelorthesis_project\\SemanticModelPrototype\\src\\main\\java\\org\\example\\vc-db-1.rdf");
    }
}