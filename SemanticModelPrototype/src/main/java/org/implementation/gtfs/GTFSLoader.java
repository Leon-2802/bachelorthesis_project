package org.implementation.gtfs;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum RouteType {
    BUS,
    DEMAND_AND_RESPONSE_BUS
}

public final class GTFSLoader {
    private GTFSLoader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Parses a GTFS text file into a Map<String, Map<String, String>>.
     *
     * @param filePath The path to the GTFS text file.
     * @param keyField The field name to use as the key for the outer map (e.g., "trip_id", "stop_id").
     * @return A map representing the GTFS data.
     * @throws IOException If there is an error reading the file.
     */
    public static Map<String, Map<String, String>> parseGTFSFileToHashMap(String filePath, String keyField) throws IOException, IllegalArgumentException {
        Map<String, Map<String, String>> dataMap = new HashMap<>();

        try (Reader reader = new FileReader(filePath)) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            // Get the header map to find the key field index
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            if (!headerMap.containsKey(keyField)) {
                throw new IllegalArgumentException("Key field not found in the header: " + keyField);
            }

            for (CSVRecord csvRecord : csvParser) {
                String key = csvRecord.get(keyField);
                Map<String, String> fieldMap = new HashMap<>();
                for (Map.Entry<String, Integer> header : headerMap.entrySet()) {
                    fieldMap.put(header.getKey(), csvRecord.get(header.getValue()));
                }
                dataMap.put(key, fieldMap);
            }
        }

        return dataMap;
    }

    public static List<Map<String, String>> parseGTFSFileToList(String filePath, String keyField) throws IOException, IllegalArgumentException{
        List<Map<String, String>> dataList = new ArrayList<>();
        try (Reader reader = new FileReader(filePath)) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            // Get the header map to find the key field index
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            if (!headerMap.containsKey(keyField)) {
                throw new IllegalArgumentException("Key field not found in the header: " + keyField);
            }

            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> fieldMap = new HashMap<>();
                for (Map.Entry<String, Integer> header : headerMap.entrySet()) {
                    fieldMap.put(header.getKey(), csvRecord.get(header.getValue()));
                }
                dataList.add(fieldMap);
            }
        }

        return dataList;
    }
    /**
     * Loads GTFS data for a specific trip into an XML document.
     *
     * @param tripId The ID of the trip to load.
     * @param tripsData A map of trip data (keyed by trip ID).
     * @param stopTimesData A list of stop time entries, each containing stop info for trips.
     * @param stopsData A map of stop details (keyed by stop ID).
     * @return An XML Document representing the trip's data.
     * @throws ParserConfigurationException if there is a parser configuration error.
     */
    public static Document loadTripToXML(String tripId, Map<String, Map<String, String>> tripsData,
                                         List<Map<String, String>> stopTimesData,
                                         Map<String, Map<String, String>> stopsData)
            throws ParserConfigurationException {

        // Create a new XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Root element
        Element rootElement = doc.createElement("Trip");
        rootElement.setAttribute("id", tripId);
        doc.appendChild(rootElement);

        // Add trip details
        Map<String, String> tripDetails = tripsData.get(tripId);
        if (tripDetails != null) {
            Element tripElement = doc.createElement("Details");
            for (Map.Entry<String, String> entry : tripDetails.entrySet()) {
                Element detail = doc.createElement(entry.getKey());
                detail.setTextContent(entry.getValue());
                tripElement.appendChild(detail);
            }
            rootElement.appendChild(tripElement);
        }

        // Add stop times
        Element stopTimesElement = doc.createElement("StopTimes");
        for (Map<String, String> stopTime : stopTimesData) {
            if (tripId.equals(stopTime.get("trip_id"))) {
                Element stopTimeElement = doc.createElement("StopTime");
                for (Map.Entry<String, String> entry : stopTime.entrySet()) {
                    Element detail = doc.createElement(entry.getKey());
                    detail.setTextContent(entry.getValue());
                    stopTimeElement.appendChild(detail);
                }
                stopTimesElement.appendChild(stopTimeElement);
            }
        }
        rootElement.appendChild(stopTimesElement);

        // Add stops
        Element stopsElement = doc.createElement("Stops");
        for (Map<String, String> stopTime : stopTimesData) {
            if (tripId.equals(stopTime.get("trip_id"))) {
                String stopId = stopTime.get("stop_id");
                Map<String, String> stopDetails = stopsData.get(stopId);
                if (stopDetails != null) {
                    Element stopElement = doc.createElement("Stop");
                    stopElement.setAttribute("id", stopId);
                    for (Map.Entry<String, String> entry : stopDetails.entrySet()) {
                        Element detail = doc.createElement(entry.getKey());
                        detail.setTextContent(entry.getValue());
                        stopElement.appendChild(detail);
                    }
                    stopsElement.appendChild(stopElement);
                }
            }
        }
        rootElement.appendChild(stopsElement);

        return doc;
    }
}