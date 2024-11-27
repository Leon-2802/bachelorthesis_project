package org.implementation;

import org.apache.jena.ontapi.OntModelFactory;
import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class OntologyService {
    private OntologyService() {}

    private static final String ontRoot = "http://www.iiius.de/ontologies/public_transit_travel_information_ontology#";

    public static OntModel loadModelFromFile(String path) {
        // An ontology model is an extension of the Jena RDF model, providing extra capabilities for handling ontologies
        OntModel model = OntModelFactory.createModel();
        model.read(path);

        return model;
    }

    public static Model getBaseModel(OntModel ontModel) {
        // base graph without imports
        return ontModel.getBaseModel();
    }

    public static OntModel mapXMlToOntology(OntModel model, Document doc) {
        // Iterate over each trip element
        NodeList tripList = doc.getElementsByTagName("trip");
        for (int i = 0; i < tripList.getLength(); i++) {
            Node tripNode = tripList.item(i);
            if (tripNode.getNodeType() == Node.ELEMENT_NODE) {
                Element tripElement = (Element) tripNode;

                String tripId = tripElement.getElementsByTagName("id").item(0).getTextContent();
                String origin = tripElement.getElementsByTagName("origin").item(0).getTextContent();
                String destination = tripElement.getElementsByTagName("destination").item(0).getTextContent();

                Resource trip = model.createResource(ontRoot + "Trip" + tripId);
                trip.addProperty(RDF.type, model.getResource(ontRoot + "Trip"));

                // Add origin and destination properties
                trip.addProperty(model.getProperty(ontRoot + "hasOrigin"), model.createResource(ontRoot + origin));
                trip.addProperty(model.getProperty(ontRoot + "hasDestination"), model.createResource(ontRoot + destination));

                // Add trip legs
                NodeList legList = tripElement.getElementsByTagName("leg");
                for (int j = 0; j < legList.getLength(); j++) {
                    String legId = legList.item(j).getTextContent();
                    Resource leg = model.createResource(ontRoot + "TripLeg" + legId);
                    leg.addProperty(RDF.type, model.getResource(ontRoot + "TripLeg"));
                    trip.addProperty(model.getProperty(ontRoot + "hasTripLeg"), leg);
                }
            }
        }

        return model;
    }
}
