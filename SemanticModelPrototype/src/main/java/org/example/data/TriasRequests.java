package org.example.data;

import org.implementation.Coord;
import org.implementation.HelperFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class TriasRequests {
    public static String locInfoReq(String locName, RequestTargets target) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/request_templates/base_locinfo_req.xml");
            // Update RequestTimestamp
            document.getElementsByTagName("siri:RequestTimestamp").item(0).setTextContent(HelperFunctions.getCurrentTime());
            // Set name of stop
            document.getElementsByTagName("LocationName").item(0).setTextContent(locName);

            switch (target) {
                case KVV_TRIAS -> {
                    return KVVRequest.sendRequest(document);
                }
                case VRN_TRIAS -> {
                    return VRNRequest.sendRequest(document);
                }
                default -> {
                    throw new Error("Request target not found");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String coordToCoordTripReq(Coord origin, Coord destination, short numOfResults, RequestTargets target) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/request_templates/coords_trip_req.xml");

            // Create an XPath factory and set it up
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            // Update RequestTimestamp
            NodeList timestampNodes = (NodeList) xpath.evaluate("//siri:RequestTimestamp", document, XPathConstants.NODESET);
            if (timestampNodes.getLength() > 0) {
                timestampNodes.item(0).setTextContent(HelperFunctions.getCurrentTime());
            }

            // Set coordinates of origin
            NodeList originLonNodes = (NodeList) xpath.evaluate("//Origin//GeoPosition/Longitude", document, XPathConstants.NODESET);
            NodeList originLatNodes = (NodeList) xpath.evaluate("//Origin//GeoPosition/Latitude", document, XPathConstants.NODESET);
            if (originLonNodes.getLength() > 0 && originLatNodes.getLength() > 0) {
                originLonNodes.item(0).setTextContent(origin.lonToString());
                originLatNodes.item(0).setTextContent(origin.latToString());
            }

            // Set coordinates of destination
            NodeList destLonNodes = (NodeList) xpath.evaluate("//Destination//GeoPosition/Longitude", document, XPathConstants.NODESET);
            NodeList destLatNodes = (NodeList) xpath.evaluate("//Destination//GeoPosition/Latitude", document, XPathConstants.NODESET);
            if (destLonNodes.getLength() > 0 && destLatNodes.getLength() > 0) {
                destLonNodes.item(0).setTextContent(destination.lonToString());
                destLatNodes.item(0).setTextContent(destination.latToString());
            }

            // Set number of results
            NodeList numOfResultsNodes = (NodeList) xpath.evaluate("//NumberOfResults", document, XPathConstants.NODESET);
            if (numOfResultsNodes.getLength() > 0) {
                numOfResultsNodes.item(0).setTextContent(String.valueOf(numOfResults));
            }

            switch (target) {
                case KVV_TRIAS -> {
                    return KVVRequest.sendRequest(document);
                }
                case VRN_TRIAS -> {
                    return VRNRequest.sendRequest(document);
                }
                default -> {
                    throw new Error("Request target not found");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String stationToStationTripRequest (String originStationRef, String destinationStationRef, short numOfResults, RequestTargets target) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/request_templates/station_trip_req.xml");

            // Create an XPath factory and set it up
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            // Update RequestTimestamp
            NodeList timestampNodes = (NodeList) xpath.evaluate("//siri:RequestTimestamp", document, XPathConstants.NODESET);
            if (timestampNodes.getLength() > 0) {
                timestampNodes.item(0).setTextContent(HelperFunctions.getCurrentTime());
            }

            // Set station references
            NodeList originStopPointRefNodes = (NodeList) xpath.evaluate("//Origin//LocationRef/StopPointRef", document, XPathConstants.NODESET);
            if(originStopPointRefNodes.getLength() > 0) {
                originStopPointRefNodes.item(0).setTextContent(originStationRef);
            }
            NodeList destStopPointRefNodes = (NodeList) xpath.evaluate("//Destination//LocationRef/StopPointRef", document, XPathConstants.NODESET);
            if(destStopPointRefNodes.getLength() > 0) {
                destStopPointRefNodes.item(0).setTextContent(destinationStationRef);
            }

            // Set number of results
            NodeList numOfResultsNodes = (NodeList) xpath.evaluate("//NumberOfResults", document, XPathConstants.NODESET);
            if (numOfResultsNodes.getLength() > 0) {
                numOfResultsNodes.item(0).setTextContent(String.valueOf(numOfResults));
            }

            switch (target) {
                case KVV_TRIAS -> {
                    return KVVRequest.sendRequest(document);
                }
                case VRN_TRIAS -> {
                    return VRNRequest.sendRequest(document);
                }
                default -> {
                    throw new Error("Request target not found");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String stopEventRequest(String stationRef, short numOfResults, RequestTargets target) {
        try {
            return "";
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
