package org.implementation.trias;

import org.implementation.HelperFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class TriasRequests {
    private DocumentBuilder docBuilder;
    private XPath xPath;
    public TriasRequests() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this.docBuilder = factory.newDocumentBuilder();
            XPathFactory xpathFactory = XPathFactory.newInstance();
            this.xPath = xpathFactory.newXPath();
        }
        catch (ParserConfigurationException ex) {
            System.out.println("ParserConfigurationException in TriasRequests constructor");
            System.out.println("Details: ");
            ex.printStackTrace();
        }
    }

    public String locInfoReq(String locName, RequestTargets target) {
        try {
            // Load the XML template
            Document document = this.docBuilder.parse("SemanticModelPrototype/src/main/java/org/implementation/trias/request_templates/base_locinfo_req.xml");
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

    public String stationToStationTripRequest (String originStationRef, String destinationStationRef, String depTime, short numOfResults, boolean realTimeData, RequestTargets target) {
        try {
            // Load the XML template
            Document document = this.docBuilder.parse("SemanticModelPrototype/src/main/java/org/implementation/trias/request_templates/station_trip_req.xml");

            // Update RequestTimestamp
            this.changeTextContent("//siri:RequestTimestamp", document, HelperFunctions.getCurrentTime());
            //... and Departure Time
            this.changeTextContent("//Origin//DepArrTime", document, depTime);

            // Set station references
            this.changeTextContent("//Origin//LocationRef/StopPointRef", document, originStationRef);
            this.changeTextContent("//Destination//LocationRef/StopPointRef", document, destinationStationRef);

            //Set realtimeData
            String realtimeDataStr = realTimeData ? "true" : "false";
            this.changeTextContent("//IncludeRealtimeData", document, realtimeDataStr);

            // Set number of results
            this.changeTextContent("//NumberOfResults", document, String.valueOf(numOfResults));

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

    private void changeTextContent(String expression, Document document, String content) {
        try {
            NodeList nodes = (NodeList) this.xPath.evaluate(expression, document, XPathConstants.NODESET);
            if(nodes.getLength() > 0) {
                nodes.item(0).setTextContent(content);
            } else {
                System.out.println("No nodes found with expression: " + expression);
            }
        }
        catch (XPathExpressionException ex) {
            System.out.println("XPathExpressionException occured in changeTextContent method");
            System.out.println("Details: " + ex.getMessage());
        }
    }
}
