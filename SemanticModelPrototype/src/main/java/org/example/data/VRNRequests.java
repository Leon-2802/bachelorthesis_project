package org.example.data;

import org.implementation.Coord;
import org.implementation.HelperFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;

public class VRNRequests {

    public static String vrnLocInfoReq(String locName) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/base_locinfo_req.xml");
            // Update RequestTimestamp
            document.getElementsByTagName("siri:RequestTimestamp").item(0).setTextContent(HelperFunctions.getCurrentTime());
            // Set name of stop
            document.getElementsByTagName("LocationName").item(0).setTextContent(locName);

            String xml = HelperFunctions.documentToString(document);
            return sendRequestVRN(xml);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String vrnCoordToCoordTripReq(Coord origin, Coord destination, String numOfResults) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/base_trip_req.xml");

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
                numOfResultsNodes.item(0).setTextContent(numOfResults);
            }

            String xml = HelperFunctions.documentToString(document);
            return sendRequestVRN(xml);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String vrnStationToStationTripRequest (String originStationRef, String destinationStationRef, String numOfResults) {
        try {
            return "";
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String vrnStopEventRequest(String stationRef, String numOfResults) {
        try {
            return "";
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String sendRequestVRN(String xmlRequest) {
        try {
            // URL of the server endpoint
            String endpointUrl = "https://www.vrn.de/service/entwickler/trias-live/";
            URL url = new URL(endpointUrl);

            // Open a connection
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            // Set the request method to POST
            connection.setRequestMethod("POST");
            // Set the request content-type header to XML
            connection.setRequestProperty("Content-Type", "text/xml; utf-8");
            // add Auth
            String apiKey = HelperFunctions.readFiletoString("SemanticModelPrototype/src/main/java/org/implementation/vrnApiKey.txt", false);
            connection.setRequestProperty("Authorization", apiKey);
            // Enable input and output streams
            connection.setDoOutput(true);
            // prevent connection from hanging indefinitely
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Write the XML request to the output stream (-> send request)
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = xmlRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the input stream
            try (InputStream is = connection.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is, "utf-8");
                 BufferedReader br = new BufferedReader(isr)) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Format to prettier XML-appearance
                return HelperFunctions.formatXML(response.toString());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
