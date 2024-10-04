package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

public class JohnSmith {
    static String personURI = "http://somewhere/JohnSmith";
    String givenName    = "John";
    String familyName   = "Smith";
    String fullName     = givenName + " " + familyName;

    Model model = ModelFactory.createDefaultModel();

    // VCARD: The property is provided by a "constant" class VCARD which holds objects representing all the definitions in the VCARD schema.
    // Jena provides constant classes for other well known schemas, such as RDF and RDF schema themselves, Dublin Core and OWL.
    Resource johnSmith = model.createResource(personURI)
            .addProperty(VCARD.FN, fullName)
            .addProperty(VCARD.N,
                    model.createResource()
                            .addProperty(VCARD.Given, givenName)
                            .addProperty(VCARD.Family, familyName));

}
