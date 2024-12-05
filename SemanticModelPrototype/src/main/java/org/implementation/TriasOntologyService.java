package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.implementation.ontologyClasses.ContinuousLeg;
import org.implementation.ontologyClasses.InterchangeLeg;
import org.implementation.ontologyClasses.Site;
import org.implementation.ontologyClasses.StopPoint;
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
import java.time.format.ResolverStyle;
import java.util.Iterator;

public class TriasOntologyService {
    private Document triasDocument;
    private final OntModel model;
    private final XPath xPath;
    public TriasOntologyService(OntModel model) {
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

    private static final String ontRoot = Constants.ontRoot;

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

            // Retrieve all TripLeg elements
            NodeList tripLegNodes = tripElement.getElementsByTagName("trias:TripLeg");
            // Loop over all TripLeg elements
            for (int i = 0; i < tripLegNodes.getLength(); i++) {
                Element tripLegElement = (Element) tripLegNodes.item(i);

                // Extract necessary information from each TripLeg element
                String tripLegId = tripLegElement.getElementsByTagName("trias:LegId").item(0).getTextContent();
                String legType = getChildTagName(tripLegElement, 2, true);
                if (legType == null) {
                    throw new IllegalArgumentException("legType element missing tripLeg with id " + tripLegId);
                }

                // Create a resource for each TripLeg
                Resource tripLeg = model.createResource(ontRoot + "TripLeg" + tripLegId);
                tripLeg.addProperty(RDF.type, model.getResource(ontRoot + legType));

                // store ontology properties:
                Property hasName = model.getProperty(ontRoot + "hasName");
                Property hasStopID = model.getProperty(ontRoot + "hasStopID");
                Property hasOrigin = model.getProperty(ontRoot + "hasTripLegOrigin");
                Property hasDestination = model.getProperty(ontRoot + "hasTripLegDestination");
                Property hasWalkDuration = model.getProperty(ontRoot + "hasWalkDuration");
                Property hasInterchangeDuration = model.getProperty(ontRoot + "hasInterchangeDuration");
                Property hasTripLeg = model.getProperty(ontRoot + "hasTripLeg");

                switch (legType) {
                    case "TimedLeg":
                        handleTimedLeg(tripLegElement);
                        break;
                    case "ContinuousLeg":
                        ContinuousLeg continuousLeg = handleContinuousLeg(tripLegElement);

                        Resource originContinuous = model.createResource(ontRoot + "OriginStopPointForTripLeg" + tripLegId);
                        originContinuous.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                        originContinuous.addLiteral(hasName, continuousLeg.getOrigin().getName());
                        originContinuous.addLiteral(hasStopID, continuousLeg.getOrigin().getStopID());
                        tripLeg.addProperty(hasOrigin, originContinuous);

                        Resource destinationContinuous = model.createResource(ontRoot + "DestinationStopPointForTripLeg" + tripLegId);
                        destinationContinuous.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                        destinationContinuous.addLiteral(hasName, continuousLeg.getDestination().getName());
                        destinationContinuous.addLiteral(hasStopID, continuousLeg.getDestination().getStopID());
                        tripLeg.addProperty(hasDestination, destinationContinuous);

                        tripLeg.addLiteral(hasWalkDuration, continuousLeg.getWalkDuration());
                        break;
                    case "InterchangeLeg":
                        InterchangeLeg interchangeLeg = handleInterchangeLeg(tripLegElement);

                        Resource originInterchange = model.createResource(ontRoot + "OriginStopPointForTripLeg" + tripLegId);
                        originInterchange.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                        originInterchange.addLiteral(hasName, interchangeLeg.getOrigin().getName());
                        originInterchange.addLiteral(hasStopID, interchangeLeg.getOrigin().getStopID());
                        tripLeg.addProperty(hasOrigin, originInterchange);

                        Resource destinationInterchange = model.createResource(ontRoot + "DestinationStopPointForTripLeg" + tripLegId);
                        destinationInterchange.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                        destinationInterchange.addLiteral(hasName, interchangeLeg.getDestination().getName());
                        destinationInterchange.addLiteral(hasStopID, interchangeLeg.getDestination().getStopID());
                        tripLeg.addProperty(hasDestination, destinationInterchange);

                        tripLeg.addLiteral(hasInterchangeDuration, interchangeLeg.getInterchangeDuration());
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported leg type: " + legType);
                }

                // Add the hasTripLeg property to the trip, referencing the TripLeg
                trip.addProperty(hasTripLeg, tripLeg);
            }

            // add Origin and Destination properties to Trip:
            //....
        }

        //save to file for testing:
        try {
            saveOntModelToOwlFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTimedLeg(Element timedLeg) {

    }
    private ContinuousLeg handleContinuousLeg(Element continuousLeg) {
        Element legStart = (Element) continuousLeg.getElementsByTagName("trias:LegStart").item(0);
        StopPoint origin = createStopPoint(legStart);

        Element legEnd = (Element) continuousLeg.getElementsByTagName("trias:LegEnd").item(0);
        StopPoint destination = createStopPoint(legEnd);

        String duration = continuousLeg.getElementsByTagName("trias:Duration").item(0).getTextContent();

        return new ContinuousLeg(origin, destination, duration);
    }
    private InterchangeLeg handleInterchangeLeg(Element interchangeLeg) {
        Element legStart = (Element) interchangeLeg.getElementsByTagName("trias:LegStart").item(0);
        StopPoint origin = createStopPoint(legStart);

        Element legEnd = (Element) interchangeLeg.getElementsByTagName("trias:LegEnd").item(0);
        StopPoint destination = createStopPoint(legEnd);

        String duration = interchangeLeg.getElementsByTagName("trias:Duration").item(0).getTextContent();

        return new InterchangeLeg(origin, destination, duration);
    }

    private StopPoint createStopPoint(Element element) {
        NodeList locationNameList = element.getElementsByTagName("trias:LocationName");
        if (locationNameList.getLength() > 0) {
            Element locationNameElement = (Element) locationNameList.item(0);

            // Get the <trias:Text> element within <trias:LocationName>
            NodeList textList = locationNameElement.getElementsByTagName("trias:Text");
            if (textList.getLength() > 0) {
                Element textElement = (Element) textList.item(0);

                // Get the value of <trias:Text>
                String textValue = textElement.getTextContent();
                StopPoint stopPoint = new StopPoint(textValue, element.getElementsByTagName("trias:StopPointRef").item(0).getTextContent());
                return stopPoint;
            } else {
                System.out.println("<trias:Text> element not found within <trias:LocationName>");
                return null;
            }
        } else {
            System.out.println("<trias:LocationName> element not found within <trias:LegStart>");
            return null;
        }
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

    private String getChildTagName(Element xmlElement, int index, boolean removePrefix) {
        NodeList childNodes = xmlElement.getChildNodes();
        int elementCount = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            // Check if the node is an element
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elementCount++;
                // If this is the second element node, return its tag name
                if (elementCount == index) {
                    if (removePrefix) {
                        return node.getNodeName().substring(node.getNodeName().lastIndexOf(":") + 1);
                    } else {
                        return node.getNodeName();
                    }
                }
            }
        }

        // Return null if there is no child element at index
        return null;
    }

    private void saveOntModelToOwlFile() throws IOException {
        try (OutputStream out = new FileOutputStream("SemanticModelPrototype/src/main/java/org/implementation/out.owl")) {
            this.model.write(out, "RDF/XML-ABBREV");
        }
    }
}
