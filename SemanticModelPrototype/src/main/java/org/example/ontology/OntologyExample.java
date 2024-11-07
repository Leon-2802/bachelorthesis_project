package org.example.ontology;

import org.apache.jena.graph.Graph;
import org.apache.jena.ontapi.GraphRepository;
import org.apache.jena.ontapi.OntModelFactory;
import org.apache.jena.ontapi.OntSpecification;
import org.apache.jena.ontapi.common.OntConfig;
import org.apache.jena.ontapi.common.OntPersonalities;
import org.apache.jena.ontapi.common.OntPersonality;
import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


// import wikidata to ontology: https://www.youtube.com/watch?v=UViYCVhds3U

public class OntologyExample {
    private static final String rdfString = """
            <rdf:RDF
                xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'
                xmlns:vCard='http://www.w3.org/2001/vcard-rdf/3.0#'
                >
                        
              <rdf:Description rdf:about="http://somewhere/JohnSmith">
                <vCard:FN>John Smith</vCard:FN>
                <vCard:N rdf:parseType="Resource">
            	    <vCard:Family>Smith</vCard:Family>
            	    <vCard:Given>John</vCard:Given>
                </vCard:N>
              </rdf:Description>
                        
            </rdf:RDF>
            """;

    public static void creatingOntologyModel() {
        // An ontology model is an extension of the Jena RDF model, providing extra capabilities for handling ontologies
        OntModel m = OntModelFactory.createModel();
    }

    public static OntModel loadModelFromString(String rdfString) {
        // Convert the RDF content string to an InputStream
        InputStream inputStream = new ByteArrayInputStream(rdfString.getBytes());

        // We will load an ontology document into an ontology model in the same way as a normal Jena model, using the read method
        Model model = ModelFactory.createDefaultModel();
        model.read(inputStream, null);

        return createOntModel(model);
    }

    public static Model getBaseModel(OntModel ontModel) {
        // base graph without imports
        return ontModel.getBaseModel();
    }

    private static OntModel createOntModel(Model m) {
        // Convert the model to a graph
        Graph graph = m.getGraph();

        GraphRepository repository = GraphRepository.createGraphDocumentRepositoryMem();
        return OntModelFactory.createModel(graph, OntSpecification.OWL2_DL_MEM_BUILTIN_RDFS_INF, repository);
    }

    // OntSpecification allows complete control over the configuration choices for the ontology model, including the language profile in use and the reasoner
    private static OntSpecification customModelSpecification(OntPersonality.Builtins builtins, OntPersonality.Reserved reserved,
                                                             OntPersonality.Punnings punnings, OntConfig ontConfig) {
        // For details about the config see: https://jena.apache.org/documentation/ontology/#creating-ontology-models
        OntPersonality OWL2_FULL_PERSONALITY = OntPersonalities.OWL2_ONT_PERSONALITY()
                .setBuiltins(builtins) // contains a set of OWL entities’ IRIs that do not require an explicit declaration (e.g., owl:Thing)
                .setReserved(reserved)
                .setPunnings(punnings)
                .setConfig(ontConfig)
                .build();

        return new OntSpecification(
                OWL2_FULL_PERSONALITY, RDFSRuleReasonerFactory.theInstance()
        );
    }
}
