package org.implementation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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