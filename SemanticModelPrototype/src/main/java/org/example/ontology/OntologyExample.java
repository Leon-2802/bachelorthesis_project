package org.example.ontology;

import org.apache.jena.ontapi.OntModelFactory;
import org.apache.jena.ontapi.OntSpecification;
import org.apache.jena.ontapi.common.OntConfig;
import org.apache.jena.ontapi.common.OntConfigs;
import org.apache.jena.ontapi.common.OntPersonalities;
import org.apache.jena.ontapi.common.OntPersonality;
import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;

public class OntologyExample {
    public static void creatingOntologyModel() {
        // An ontology model is an extension of the Jena RDF model, providing extra capabilities for handling ontologies
        OntModel m = OntModelFactory.createModel();
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
