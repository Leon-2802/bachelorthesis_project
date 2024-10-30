package org.example.data;

import org.implementation.HelperFunctions;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class KVVRequests {
    private static String sendRequestKVV(String xmlRequest) {
        try {
            // URL of the server endpoint
            String endpointUrl = "https://projekte.kvv-efa.de/gobberttrias/trias";
            URL url = new URL(endpointUrl);

            // Open a connection
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            // Set the request method to POST
            connection.setRequestMethod("POST");
            // Set the request content-type header to XML
            connection.setRequestProperty("Content-Type", "text/xml; utf-8");
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
}
