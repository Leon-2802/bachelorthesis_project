package org.example.data;

import org.implementation.HelperFunctions;
import org.w3c.dom.Document;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;

public class VRNRequests {

    public static void vrnBasic() {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/base_stop_req.xml");

            // Update RequestTimestamp
            document.getElementsByTagName("siri:RequestTimestamp").item(0).setTextContent(HelperFunctions.getCurrentTime());

            // Convert the Document back to a string
            String xml = HelperFunctions.documentToString(document);
            sendRequestVRN(xml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void vrnStopReq(String stopName) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/base_stop_req.xml");
            // Update RequestTimestamp
            document.getElementsByTagName("siri:RequestTimestamp").item(0).setTextContent(HelperFunctions.getCurrentTime());
            // Set name of stop
            document.getElementsByTagName("LocationName").item(0).setTextContent(stopName);

            String xml = HelperFunctions.documentToString(document);
            sendRequestVRN(xml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void vrnTripReq(String numOfResults) {
        try {
            // Load the XML template
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("SemanticModelPrototype/src/main/java/org/example/data/base_trip_req.xml");

            // Update RequestTimestamp
            document.getElementsByTagName("siri:RequestTimestamp").item(0).setTextContent(HelperFunctions.getCurrentTime());
            document.getElementsByTagName("NumberOfResults").item(0).setTextContent(numOfResults);

            String xml = HelperFunctions.documentToString(document);
            sendRequestVRN(xml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequestVRN(String xmlRequest) {
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
                String formattedRes = HelperFunctions.formatXML(response.toString());
                System.out.println("Response: " + formattedRes);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
