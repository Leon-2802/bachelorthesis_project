package org.example.ontology;

import org.apache.jena.ontapi.OntModelFactory;
import org.apache.jena.ontapi.OntSpecification;
import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.ontapi.model.OntStatement;

public class OntModelTripleRepresentation {
    public static OntModel example() {
        OntModel m = OntModelFactory.createModel( OntSpecification.OWL2_DL_MEM );
        OntStatement st1 = m.createOntClass("http://example.org/X").getMainStatement();
        OntStatement st2 = st1.addAnnotation(m.getRDFSComment(), "comment#1");
        OntStatement st3 = st2.addAnnotation(m.getRDFSLabel(), "label#1");
        OntStatement st4 = st3.addAnnotation(m.getRDFSLabel(), "label#2");

        return m;
    }
}
