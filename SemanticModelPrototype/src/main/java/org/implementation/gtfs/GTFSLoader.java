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
                directConnection.appendChild(createTripElement(doc, this.tripsData.get(tripId), startStopId, endStopId));
                rootElement.appendChild(directConnection);
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
                                        transferConnection.appendChild(createTripElement(doc, this.tripsData.get(startTrip), startStopId, transfer.get("from_stop_id")));
                                        transferConnection.appendChild(createTransferElement(doc, transfer));
                                        transferConnection.appendChild(createTripElement(doc, this.tripsData.get(endTrip), transfer.get("to_stop_id"), endStopId));
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

    private Element createTripElement(Document doc, Map<String, String> tripData, String startStop, String endStop) {
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
        Map<String, String> routeData = this.routesData.get(tripData.get("route_id"));
        for (Map.Entry<String, String> entry : routeData.entrySet()) {
            Element dataElement = doc.createElement(entry.getKey());
            dataElement.appendChild(doc.createTextNode(entry.getValue()));
            routeElement.appendChild(dataElement);
        }
        tripElement.appendChild(routeElement);

        Element startStopElement = createStopElement(doc, startStop, "OriginStop");
        Element endStopElement = createStopElement(doc, endStop, "DestinationStop");
        tripElement.appendChild(startStopElement);
        tripElement.appendChild(endStopElement);

        Element stopTimesElement = doc.createElement("StopTimes");
        boolean inRange = false;
        for (Map<String, String> stopTime : this.stopTimesData) {
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

    private Element createTransferElement(Document doc, Map<String, String> transferData) {
        Element transferElement = doc.createElement("Transfer");
        for (Map.Entry<String, String> entry : transferData.entrySet()) {
            Element dataElement = doc.createElement(entry.getKey());
            dataElement.appendChild(doc.createTextNode(entry.getValue()));
            transferElement.appendChild(dataElement);
        }
        // get data of origin and destination stop
        Element originStopElement = createStopElement(doc, transferData.get("from_stop_id"), "OriginStop");
        Element destinationStopElement = createStopElement(doc, transferData.get("to_stop_id"), "DestinationStop");
        transferElement.appendChild(originStopElement);
        transferElement.appendChild(destinationStopElement);
        return transferElement;
    }

    private Element createStopElement(Document doc, String stopId, String tagName) {
        Element stopElement = doc.createElement(tagName);
        Map<String, String> stopData = this.stopsData.get(stopId);
        for (Map.Entry<String, String> entry : stopData.entrySet()) {
            Element dataElement = doc.createElement(entry.getKey());
            dataElement.appendChild(doc.createTextNode(entry.getValue()));
            stopElement.appendChild(dataElement);
        }
        return stopElement;
    }
}