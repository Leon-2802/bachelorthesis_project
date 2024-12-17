package org.implementation.gtfs;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.shared.NotFoundException;
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

public class GTFSLoader {
    private final Map<String, Map<String, String>> tripsData;
    private final Map<String, Map<String, String>> routesData;
    private final Map<String, Map<String, String>> stopsData;
    private final List<Map<String, String>> stopTimesData;
    private final List<Map<String, String>> transferData;

    public GTFSLoader() {
        try {
            tripsData = this.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/trips.txt", "trip_id");
            routesData = this.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/routes.txt", "route_id");
            stopsData = this.parseGTFSFileToHashMap("data/fluo-grand-est-fluo67-gtfs/stops.txt", "stop_id");
            transferData = this.parseGTFSFileToList("data/fluo-grand-est-fluo67-gtfs/transfers.txt", "to_stop_id");
            stopTimesData = this.parseGTFSFileToList("data/fluo-grand-est-fluo67-gtfs/stop_times.txt", "trip_id");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Map<String, String>> parseGTFSFileToHashMap(String filePath, String keyField) throws IOException, IllegalArgumentException {
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

    private List<Map<String, String>> parseGTFSFileToList(String filePath, String keyField) throws IOException, IllegalArgumentException{
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

    public Document extractConnectionDataToXML(String startStopId, String endStopId) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("GTFSConnectionData");
        doc.appendChild(rootElement);

        boolean connectionFound = false;

        // Find trips that include the start stop
        List<String> startTrips = new ArrayList<>();
        for (Map<String, String> stopTime : this.stopTimesData) {
            if (stopTime.get("stop_id").equals(startStopId)) {
                startTrips.add(stopTime.get("trip_id"));
            }
        }

        // Find trips that include the end stop
        List<String> endTrips = new ArrayList<>();
        for (Map<String, String> stopTime : this.stopTimesData) {
            if (stopTime.get("stop_id").equals(endStopId)) {
                endTrips.add(stopTime.get("trip_id"));
            }
        }

        // Check for direct connection
        for (String tripId : startTrips) {
            if (endTrips.contains(tripId)) {
                Element directConnection = doc.createElement("DirectConnection");
                directConnection.appendChild(createTripElement(doc, this.tripsData.get(tripId), startStopId, endStopId, this.routesData, this.stopTimesData));
                rootElement.appendChild(directConnection);
                connectionFound = true;
                return doc;
            }
        }

        // Check for transfers
        for (String startTrip : startTrips) {
            for (Map<String, String> stopTime : this.stopTimesData) {
                if (stopTime.get("trip_id").equals(startTrip)) {
                    String transferStop = stopTime.get("stop_id");
                    for (Map<String, String> transfer : this.transferData) {
                        if (transfer.get("from_stop_id").equals(transferStop)) {
                            String toStop = transfer.get("to_stop_id");
                            for (String endTrip : endTrips) {
                                for (Map<String, String> endStopTime : this.stopTimesData) {
                                    if (endStopTime.get("trip_id").equals(endTrip) && endStopTime.get("stop_id").equals(toStop)) {
                                        Element transferConnection = doc.createElement("TransferConnection");
                                        transferConnection.appendChild(createTripElement(doc, this.tripsData.get(startTrip), startStopId, transfer.get("from_stop_id"), this.routesData, this.stopTimesData));
                                        transferConnection.appendChild(createTransferElement(doc, transfer));
                                        transferConnection.appendChild(createTripElement(doc, this.tripsData.get(endTrip), transfer.get("to_stop_id"), endStopId, this.routesData, this.stopTimesData));
                                        rootElement.appendChild(transferConnection);
                                        return doc;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new Exception("No connection found between stops: " + startStopId + " and " + endStopId);
    }

    private static Element createTripElement(Document doc, Map<String, String> tripData, String startStop, String endStop, Map<String, Map<String, String>> routesData, List<Map<String, String>> stopTimesData) {
        Element tripElement = doc.createElement("Trip");
        tripElement.setAttribute("id", tripData.get("trip_id"));

        Element detailElement = doc.createElement("Details");
        for (Map.Entry<String, String> entry : tripData.entrySet()) {
            Element detail = doc.createElement(entry.getKey());
            detail.setTextContent(entry.getValue());
            detailElement.appendChild(detail);
        }
        tripElement.appendChild(detailElement);

        Element routeElement = doc.createElement("Route");
        Map<String, String> routeData = routesData.get(tripData.get("route_id"));
        for (Map.Entry<String, String> entry : routeData.entrySet()) {
            Element dataElement = doc.createElement(entry.getKey());
            dataElement.appendChild(doc.createTextNode(entry.getValue()));
            routeElement.appendChild(dataElement);
        }
        tripElement.appendChild(routeElement);

        Element stopTimesElement = doc.createElement("StopTimes");
        boolean inRange = false;
        for (Map<String, String> stopTime : stopTimesData) {
            if (stopTime.get("trip_id").equals(tripData.get("trip_id"))) {
                if (stopTime.get("stop_id").equals(startStop)) inRange = true;
                if (inRange) {
                    Element stopTimeElement = doc.createElement("StopTime");
                    for (Map.Entry<String, String> entry : stopTime.entrySet()) {
                        Element dataElement = doc.createElement(entry.getKey());
                        dataElement.appendChild(doc.createTextNode(entry.getValue()));
                        stopTimeElement.appendChild(dataElement);
                    }
                    stopTimesElement.appendChild(stopTimeElement);
                }
                if (stopTime.get("stop_id").equals(endStop)) inRange = false;
            }
        }
        tripElement.appendChild(stopTimesElement);

        return tripElement;
    }

    private static Element createTransferElement(Document doc, Map<String, String> transferData) {
        Element transferElement = doc.createElement("Transfer");
        for (Map.Entry<String, String> entry : transferData.entrySet()) {
            Element dataElement = doc.createElement(entry.getKey());
            dataElement.appendChild(doc.createTextNode(entry.getValue()));
            transferElement.appendChild(dataElement);
        }
        return transferElement;
    }

    public void findConnection(String startStopId, String endStopId) throws NotFoundException {

        // Find trips that include the start stop
        List<String> startTrips = new ArrayList<>();
        for (Map<String, String> stopTime :this.stopTimesData) {
            if (stopTime.get("stop_id").equals(startStopId)) {
                startTrips.add(stopTime.get("trip_id"));
            }
        }

        // Find trips that include the end stop
        List<String> endTrips = new ArrayList<>();
        for (Map<String, String> stopTime : this.stopTimesData) {
            if (stopTime.get("stop_id").equals(endStopId)) {
                endTrips.add(stopTime.get("trip_id"));
            }
        }

        // Check for direct connection
        for (String tripId : startTrips) {
            if (endTrips.contains(tripId)) {
                System.out.println("Direct connection found on trip: " + tripId);
                return;
            }
        }

        // Check for transfers
        for (String startTrip : startTrips) {
            for (Map<String, String> stopTime : this.stopTimesData) {
                if (stopTime.get("trip_id").equals(startTrip)) {
                    String transferStop = stopTime.get("stop_id");
                    for (Map<String, String> transfer : this.transferData) {
                        if (transfer.get("from_stop_id").equals(transferStop)) {
                            String toStop = transfer.get("to_stop_id");
                            for (String endTrip : endTrips) {
                                for (Map<String, String> endStopTime : this.stopTimesData) {
                                    if (endStopTime.get("trip_id").equals(endTrip) && endStopTime.get("stop_id").equals(toStop)) {
                                        // load trip 01
                                        // load transfer
                                        // load trip 02
                                        System.out.println("Connection found with transfer at stop: " + transferStop);
                                        System.out.println("Trips on connection: " + startTrip +", " + endTrip);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // If no connection is found
        throw new NotFoundException("No connection found between stops: " + startStopId + " and " + endStopId);
    }

    public Document loadTripToXML(String tripId)
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
        Map<String, String> tripDetails = this.tripsData.get(tripId);
        String routeId = "";
        if (tripDetails != null) {
            Element tripElement = doc.createElement("Details");
            for (Map.Entry<String, String> entry : tripDetails.entrySet()) {
                Element detail = doc.createElement(entry.getKey());
                if (entry.getKey().equals("route_id")) {
                    routeId = entry.getValue();
                }
                detail.setTextContent(entry.getValue());
                tripElement.appendChild(detail);
            }
            rootElement.appendChild(tripElement);
        }

        // Add route details
        Map<String, String> routeDetails = this.routesData.get(routeId);
        if (routeDetails != null) {
            Element routeElement = doc.createElement("TripRoute");
            for (Map.Entry<String, String> entry : routeDetails.entrySet()) {
                Element detail = doc.createElement(entry.getKey());
                detail.setTextContent(entry.getValue());
                routeElement.appendChild(detail);
            }
            rootElement.appendChild(routeElement);
        }


        // Add stop times
        Element stopTimesElement = doc.createElement("StopTimes");
        for (Map<String, String> stopTime : this.stopTimesData) {
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
        for (Map<String, String> stopTime : this.stopTimesData) {
            if (tripId.equals(stopTime.get("trip_id"))) {
                String stopId = stopTime.get("stop_id");
                Map<String, String> stopDetails = this.stopsData.get(stopId);
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