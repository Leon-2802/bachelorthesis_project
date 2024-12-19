package org.implementation.gtfs;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.implementation.Constants;
import org.implementation.ontologyClasses.BoardAlight;
import org.implementation.ontologyClasses.InterchangeLeg;
import org.implementation.ontologyClasses.StopPoint;
import org.implementation.ontologyClasses.TimedLeg;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GTFSOntologyService {
    private Document gtfsDocument;
    private final OntModel model;
    private final XPath xPath;
    private final DocumentBuilder docBuilder;
    private static final String ontRoot = Constants.ontRoot;

    public GTFSOntologyService(OntModel model) {
        this.model = model;
        // Initialize other necessary components
        XPathFactory xpathFactory = XPathFactory.newInstance();
        this.xPath = xpathFactory.newXPath();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void mapGtfsToOntology(Document doc) throws IllegalArgumentException {
        this.gtfsDocument = doc;

        Element connectionElement = (Element) doc.getElementsByTagName("TransferConnection").item(0);
        if (connectionElement == null) {
            connectionElement = (Element) doc.getElementsByTagName("DirectConnection").item(0);
            if (connectionElement == null) throw new IllegalArgumentException("No connection found between given origin and destination");
        }
        //... Interchanges
        NodeList transfers = connectionElement.getElementsByTagName("Transfer");
        String interchanges = String.valueOf(transfers.getLength());

        Resource trip = model.createResource(ontRoot + "TripGTFS"); // be careful of the difference between trip in gtfs and trip in ontology!
        trip.addProperty(RDF.type, model.getResource(ontRoot + "Trip"));

        // store ontology properties:
        Property hasDuration = model.getProperty(ontRoot + "hasDuration");
        Property hasStartTime = model.getProperty(ontRoot + "hasStartTime");
        Property hasEndTime = model.getProperty(ontRoot + "hasEndTime");
        Property hasInterchanges = model.getProperty(ontRoot + "hasInterchanges");
        Property hasName = model.getProperty(ontRoot + "hasName");
        Property hasStopID = model.getProperty(ontRoot + "hasStopID");
        Property hasOrigin = model.getProperty(ontRoot + "hasTripLegOrigin");
        Property hasDestination = model.getProperty(ontRoot + "hasTripLegDestination");
        Property hasWalkDuration = model.getProperty(ontRoot + "hasWalkDuration");
        Property hasInterchangeDuration = model.getProperty(ontRoot + "hasInterchangeDuration");
        Property hasTripLeg = model.getProperty(ontRoot + "hasTripLeg");
        Property bikesAllowed = model.getProperty(ontRoot + "bikesAllowed");
        Property hasInformation = model.getProperty(ontRoot + "hasInformation");
        Property hasLine = model.getProperty(ontRoot + "hasLine");
        Property wheelchairAccessible = model.getProperty(ontRoot + "wheelchairAccessible");
        Property hasTransitMode = model.getProperty(ontRoot + "hasTransitMode");
        Property hasStop = model.getProperty(ontRoot + "hasStop");
        Property hasStopSequence = model.getProperty(ontRoot + "hasStopSequence");
        Property hasPlannedBay = model.getProperty(ontRoot + "hasPlannedBay");
        Property hasScheduledTime = model.getProperty(ontRoot + "hasScheduledTime");
        Property hasBoard = model.getProperty(ontRoot + "hasBoard");
        Property hasAlight = model.getProperty(ontRoot + "hasAlight");
        // ... add other literals
        trip.addLiteral(hasInterchanges, interchanges);

        NodeList tripList = doc.getElementsByTagName("Trip");
        int tripLegSequenceId = 0;
        for (int i = 0; i < tripList.getLength(); i++) {
            tripLegSequenceId++;
            Element tripElement = (Element) tripList.item(i);

            TimedLeg timedLeg = addTimedLeg(tripElement, String.valueOf(tripLegSequenceId));

            if(timedLeg != null) {
                Resource timedLegResource = model.createResource(ontRoot + "TripLeg" + tripLegSequenceId);
                timedLegResource.addProperty(RDF.type, model.getResource(ontRoot + "TimedLeg"));
                Property hasTripLegSequenceID = model.getProperty(ontRoot + "hasTripLegSequenceID");
                timedLegResource.addLiteral(hasTripLegSequenceID, String.valueOf(tripLegSequenceId));

                Resource originTimed = model.createResource(ontRoot + "OriginStopPointForTripLeg" + tripLegSequenceId);
                originTimed.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                originTimed.addLiteral(hasName, timedLeg.getOrigin().getName());
                originTimed.addLiteral(hasStopID, timedLeg.getOrigin().getStopID());
                timedLegResource.addProperty(hasOrigin, originTimed);

                Resource destinationTimed = model.createResource(ontRoot + "DestinationStopPointForTripLeg" + tripLegSequenceId);
                destinationTimed.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                destinationTimed.addLiteral(hasName, timedLeg.getDestination().getName());
                destinationTimed.addLiteral(hasStopID, timedLeg.getDestination().getStopID());
                timedLegResource.addProperty(hasDestination, destinationTimed);

                // board and alight:
                Resource board = model.createResource(ontRoot + "BoardForTripLeg" + tripLegSequenceId);
                board.addProperty(RDF.type, model.getResource(ontRoot + "BoardAlight"));
                board.addProperty(hasStop, originTimed);
                board.addLiteral(hasStopSequence, timedLeg.getBoard().getStopSequence());
                board.addLiteral(hasPlannedBay, timedLeg.getBoard().getPlannedBay());
                board.addLiteral(hasScheduledTime, timedLeg.getBoard().getScheduledTime());
                timedLegResource.addProperty(hasBoard, board);

                Resource alight = model.createResource(ontRoot + "AlightForTripLeg" + tripLegSequenceId);
                alight.addProperty(RDF.type, model.getResource(ontRoot + "BoardAlight"));
                alight.addProperty(hasStop, destinationTimed);
                alight.addLiteral(hasStopSequence, timedLeg.getAlight().getStopSequence());
                alight.addLiteral(hasPlannedBay, timedLeg.getAlight().getPlannedBay());
                alight.addLiteral(hasScheduledTime, timedLeg.getAlight().getScheduledTime());
                timedLegResource.addProperty(hasAlight, alight);

                Resource transitMode = model.getResource(ontRoot + timedLeg.getTransitMode());
                timedLegResource.addProperty(hasTransitMode, transitMode);
                timedLegResource.addLiteral(hasLine, timedLeg.getLine());
                for (int j = 0; j < timedLeg.getInformation().size(); j++) {
                    timedLegResource.addLiteral(hasInformation, timedLeg.getInformation().get(j));
                }

                trip.addProperty(hasTripLeg, timedLegResource);
            }



            if (i < transfers.getLength()) {
                tripLegSequenceId++;
                Element transferElement = (Element) transfers.item(i);
                InterchangeLeg interchangeLeg = addInterchangeLeg(transferElement);
                Resource interchangeLegResource = model.createResource(ontRoot + "TripLeg" + tripLegSequenceId);
                interchangeLegResource.addProperty(RDF.type, model.getResource(ontRoot + "InterchangeLeg"));
                Property hasTripLegSequenceID = model.getProperty(ontRoot + "hasTripLegSequenceID");
                interchangeLegResource.addLiteral(hasTripLegSequenceID, String.valueOf(hasTripLegSequenceID));

                Resource originInterchange = model.createResource(ontRoot + "OriginStopPointForTripLeg" + tripLegSequenceId);
                originInterchange.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                originInterchange.addLiteral(hasName, interchangeLeg.getOrigin().getName());
                originInterchange.addLiteral(hasStopID, interchangeLeg.getOrigin().getStopID());
                interchangeLegResource.addProperty(hasOrigin, originInterchange);

                Resource destinationInterchange = model.createResource(ontRoot + "DestinationStopPointForTripLeg" + tripLegSequenceId);
                destinationInterchange.addProperty(RDF.type, model.getResource(ontRoot + "StopPoint"));
                destinationInterchange.addLiteral(hasName, interchangeLeg.getDestination().getName());
                destinationInterchange.addLiteral(hasStopID, interchangeLeg.getDestination().getStopID());
                interchangeLegResource.addProperty(hasDestination, destinationInterchange);

                interchangeLegResource.addLiteral(hasInterchangeDuration, interchangeLeg.getInterchangeDuration());

                trip.addProperty(hasTripLeg, interchangeLegResource);
            }

            // note that there are not continuous legs in gtfs data, so those are not handled here
        }

        // add startTime and endTime here

    }

    private TimedLeg addTimedLeg(Element gtfsTrip, String tripLegSequenceId) {
        Element details = (Element) gtfsTrip.getElementsByTagName("Details").item(0);
        Element route = (Element) gtfsTrip.getElementsByTagName("Route").item(0);
        Element origin = (Element)  gtfsTrip.getElementsByTagName("OriginStop").item(0);
        Element destination = (Element)  gtfsTrip.getElementsByTagName("DestinationStop").item(0);
        Element stoptimes = (Element) gtfsTrip.getElementsByTagName("StopTimes").item(0);

        if (stoptimes.getChildNodes().getLength() <= 1) {
            // if one or less child elements in stoptimes element -> does not count as timedLeg
            return null;
        }

        String line = "";
        line += details.getChildNodes().item(3).getTextContent();
        line += ", Direction: " + details.getChildNodes().item(8).getTextContent();

        String transitMode = matchTransitMode(route.getChildNodes().item(4).getTextContent());

        List<String> information = new ArrayList<String>();
        String wheelchairAccessibility = details.getChildNodes().item(4).getTextContent();
        String wheelchairInfo = "";
        if (wheelchairAccessibility != null) {
            if (wheelchairAccessibility.equals("0")) {
                wheelchairInfo = "Wheelchair accessibility is not provided";
                information.add(wheelchairInfo);
            }
            else if (wheelchairAccessibility.equals("1")) {
                wheelchairInfo = "Wheelchair accessibility is provided";
                information.add(wheelchairInfo);
            }
        }
        String bikeAccessibility = details.getChildNodes().item(5).getTextContent();
        String bikeInfo = "";
        if (bikeAccessibility != null)  {
            if (bikeAccessibility.equals("0")) {
                bikeInfo = "Bike transportation is not allowed";
                information.add(bikeInfo);
            }
            else if (bikeAccessibility.equals("1")) {
                bikeInfo = "Bike transportation is possible";
                information.add(bikeInfo);
            }
        }
        // maybe add agency information here

        // board an alight:
        Element firstStopTime = (Element) stoptimes.getChildNodes().item(0);;
        Element lastStopTime = (Element) stoptimes.getChildNodes().item(stoptimes.getChildNodes().getLength() - 1);
        BoardAlight board = createBoardAlight(firstStopTime, origin, true);
        BoardAlight alight = createBoardAlight(lastStopTime, destination, false);
        // adjust stop sequence variable of board and alight:
        try {
            int boardStopSequence = Integer.parseInt(board.getStopSequence());
            int alightStopSequence = Integer.parseInt(alight.getStopSequence());
            int adjustedAlightStopSequence = alightStopSequence - boardStopSequence;
            alight.setStopSequence(String.valueOf(adjustedAlightStopSequence));
            board.setStopSequence("1");
        } catch (NumberFormatException ex) {
            alight.setStopSequence("N/A");
            board.setStopSequence("N/A");
        }

        return new TimedLeg(board.getStopPoint(), alight.getStopPoint(), board, alight, line, transitMode, information);
    }
    private InterchangeLeg addInterchangeLeg(Element gtfsTransfer) {
        Element originStop = (Element) gtfsTransfer.getElementsByTagName("OriginStop").item(0);
        String originStopName = originStop.getChildNodes().item(9).getTextContent();
        String originStopId = originStop.getChildNodes().item(3).getTextContent();
        StopPoint origin = new StopPoint(originStopName, originStopId);

        Element destinationStop = (Element) gtfsTransfer.getElementsByTagName("DestinationStop").item(0);
        String destinationStopName = destinationStop.getChildNodes().item(9).getTextContent();
        String destinationStopId = destinationStop.getChildNodes().item(3).getTextContent();
        StopPoint destination = new StopPoint(destinationStopName, destinationStopId);

        String duration = gtfsTransfer.getElementsByTagName("min_transfer_time").item(0).getTextContent();
        if (duration == null || duration.equals("")) duration = "N/A";

        return new InterchangeLeg(origin, destination, duration);
    }

    private BoardAlight createBoardAlight(Element stopTime, Element stop, boolean board) {
        String stopPointName = stop.getChildNodes().item(9).getTextContent();
        String stopPointId = stop.getChildNodes().item(3).getTextContent();
        StopPoint stopPoint = new StopPoint(stopPointName, stopPointId);

        int childIndex = board ? 2 : 6;
        String scheduledTime = stopTime.getChildNodes().item(childIndex).getTextContent();
        String stopSequence = stopTime.getChildNodes().item(3).getTextContent();

        return new BoardAlight(stopPoint, scheduledTime, "N/A", stopSequence);
    }

    private String matchTransitMode(String gtfsType) {
        return switch (gtfsType) {
            case "3" -> "Bus";
            case "715" -> "DemandAndResponseBus";
            default -> "UnknownTransitMode";
        };
    }

    private void saveOntModelToOwlFile() throws IOException {
        try (OutputStream out = new FileOutputStream("SemanticModelPrototype/src/main/java/org/implementation/out.owl")) {
            this.model.write(out, "RDF/XML-ABBREV");
        }
    }
}
