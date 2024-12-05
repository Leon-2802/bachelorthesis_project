package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class OntologyService {
    private Document triasDocument;
    private final OntModel model;
    private final XPath xPath;
    public OntologyService(OntModel model) {
        this.model = model;
        NamespaceContext nsContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                switch (prefix) {
                    case "trias":
                        return "http://www.vdv.de/trias";
                    case "siri":
                        return "http://www.siri.org.uk/siri";
                    // Add other namespaces here as needed
                    default:
                        return null;
                }
            }
            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }
            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        };
        XPathFactory xpathFactory = XPathFactory.newInstance();
        this.xPath = xpathFactory.newXPath();
        this.xPath.setNamespaceContext(nsContext);
    }

    private static final String ontRoot = "http://www.iiius.de/ontologies/public_transit_travel_information_ontology#";

    public void mapTriasResToOntology(Document doc) {
        this.triasDocument = doc;

        Node tripNode = getNodeByPath("//trias:Trip");
        if (tripNode.getNodeType() == Node.ELEMENT_NODE) {
            Element tripElement = (Element) tripNode;

            String tripId = tripElement.getElementsByTagName("trias:TripId").item(0).getTextContent();
            String duration = tripElement.getElementsByTagName("trias:Duration").item(0).getTextContent();
            String startTime = tripElement.getElementsByTagName("trias:StartTime").item(0).getTextContent();
            String endTime = tripElement.getElementsByTagName("trias:EndTime").item(0).getTextContent();
            String interchanges = tripElement.getElementsByTagName("trias:Interchanges").item(0).getTextContent();

            Resource trip = model.createResource(ontRoot + "Trip" + tripId);
            trip.addProperty(RDF.type, model.getResource(ontRoot + "Trip"));

            Property hasDuration = model.getProperty(ontRoot + "hasDuration");
            Property hasStartTime = model.getProperty(ontRoot + "hasStartTime");
            Property hasEndTime = model.getProperty(ontRoot + "hasEndTime");
            Property hasInterchanges = model.getProperty(ontRoot + "hasInterchanges");
            trip.addLiteral(hasDuration, duration);
            trip.addLiteral(hasStartTime, startTime);
            trip.addLiteral(hasEndTime, endTime);
            trip.addLiteral(hasInterchanges, interchanges);

            // Add trip legs
//            NodeList legList = tripElement.getElementsByTagName("leg");
//            for (int j = 0; j < legList.getLength(); j++) {
//                String legId = legList.item(j).getTextContent();
//                Resource leg = model.createResource(ontRoot + "TripLeg" + legId);
//                leg.addProperty(RDF.type, model.getResource(ontRoot + "TripLeg"));
//                trip.addProperty(model.getProperty(ontRoot + "hasTripLeg"), leg);
//            }
        }

        //save to file for testing:
        try {
            saveOntModelToOwlFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTimedLeg() {

    }
    private void handleContinousLeg() {

    }
    private void handleInterchangeLeg() {

    }

    private Node getNodeByPath(String path) {
        try {
            NodeList nodes = (NodeList) this.xPath.evaluate(path, this.triasDocument, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                return nodes.item(0);
            } else {
                System.out.println("No nodes found with expression: " + path);
                return null;
            }
        }
        catch (XPathExpressionException ex) {
            System.out.println("XPathExpressionException occured in changeTextContent method");
            System.out.println("Details: " + ex.getMessage());
            return null;
        }
    }

    private void saveOntModelToOwlFile() throws IOException {
        try (OutputStream out = new FileOutputStream("SemanticModelPrototype/src/main/java/org/implementation/out.owl")) {
            this.model.write(out, "RDF/XML-ABBREV");
        }
    }
}
