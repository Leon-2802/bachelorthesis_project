package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.query.*;

public final class SparQLService {

    private SparQLService( ) {}

    public static void sendQuery(OntModel model) {
        String queryString =
            "PREFIX j0: <http://www.iiius.de/ontologies/public_transit_travel_information_ontology#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> " +
                    "\n" +
                    "SELECT ?trip ?legType ?tripLegSequenceID ?walkDuration ?interchangeDuration ?originStopID ?originStopName ?destinationStopID ?destinationStopName ?line ?transitMode ?realtimeData ?scheduledTimeBoard ?plannedBayBoard ?stopSequenceBoard ?scheduledTimeAlight ?plannedBayAlight ?stopSequenceAlight (GROUP_CONCAT(?info; SEPARATOR=\", \") AS ?infos)\n" +
                    "WHERE {\n" +
                    "  ?trip rdf:type j0:Trip ;\n" +
                    "        j0:hasTripLeg ?leg .\n" +
                    "  \n" +
                    "  ?leg rdf:type ?legType .\n" +
                    "  \n" +
                    "  ?leg j0:hasTripLegSequenceID ?tripLegSequenceID .\n" +
                    "  OPTIONAL { ?leg j0:hasWalkDuration ?walkDuration . }\n" +
                    "  OPTIONAL { ?leg j0:hasInterchangeDuration ?interchangeDuration . }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasTripLegOrigin ?origin .\n" +
                    "    ?origin j0:hasStopID ?originStopID .\n" +
                    "    ?origin j0:hasName ?originStopName .\n" +
                    "  }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasTripLegDestination ?destination .\n" +
                    "    ?destination j0:hasStopID ?destinationStopID .\n" +
                    "    ?destination j0:hasName ?destinationStopName .\n" +
                    "  }\n" +
                    "  OPTIONAL { ?leg j0:hasLine ?line . }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasTransitMode ?mode .\n" +
                    "    ?mode rdfs:label ?transitMode .\n" +
                    "    FILTER (lang(?transitMode) = \"en\") \n" +
                    "  }\n" +
                    "  OPTIONAL { ?leg j0:realtimeData ?realtimeData . }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasBoard ?board .\n" +
                    "    ?board j0:hasScheduledTime ?scheduledTimeBoard ;\n" +
                    "           j0:hasPlannedBay ?plannedBayBoard ;\n" +
                    "           j0:hasStopSequence ?stopSequenceBoard .\n" +
                    "  }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasAlight ?alight .\n" +
                    "    ?alight j0:hasScheduledTime ?scheduledTimeAlight ;\n" +
                    "           j0:hasPlannedBay ?plannedBayAlight ;\n" +
                    "           j0:hasStopSequence ?stopSequenceAlight .\n" +
                    "  }\n" +
                    "  OPTIONAL { ?leg j0:hasInformation ?info . }\n" +
                    "}\n" +
                    "GROUP BY ?trip ?legType ?tripLegSequenceID ?walkDuration ?interchangeDuration ?originStopID ?originStopName ?destinationStopID ?destinationStopName ?line ?transitMode ?realtimeData ?scheduledTimeBoard ?plannedBayBoard ?stopSequenceBoard ?scheduledTimeAlight ?plannedBayAlight ?stopSequenceAlight\n" +
                    "ORDER BY ?tripLegSequenceID\n";

        // Create and execute the query
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            printResultsToConsole(results);
        }
        catch (Exception e) {
            System.out.println("Problem executing query: " + e.getMessage());
        }
    }

    private static void printResultsToConsole(ResultSet results) {
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            System.out.println("Trip: " + soln.get("trip"));
            System.out.println("Trip Leg Sequence ID: " + soln.get("tripLegSequenceID"));
            String legType = String.valueOf(soln.get("legType"));
            System.out.println("Leg type: " + legType);
            if (soln.contains("originStopID")) {
                System.out.println("  Origin Stop ID: " + soln.get("originStopID"));
            } else {
                System.out.println("  Origin Stop ID: N/A");
            }

            if (soln.contains("originStopName")) {
                System.out.println("  Origin Stop Name: " + soln.get("originStopName"));
            } else {
                System.out.println("  Origin Stop Name: N/A");
            }

            if (soln.contains("destinationStopID")) {
                System.out.println("  Destination Stop ID: " + soln.get("destinationStopID"));
            } else {
                System.out.println("  Destination Stop ID: N/A");
            }

            if (soln.contains("destinationStopName")) {
                System.out.println("  Destination Stop Name: " + soln.get("destinationStopName"));
            } else {
                System.out.println("  Destination Stop Name: N/A");
            }

            switch (legType) {
                case "http://www.iiius.de/ontologies/public_transit_travel_information_ontology#ContinuousLeg" -> {
                    if (soln.contains("walkDuration")) {
                        System.out.println("  Walk Duration: " + soln.get("walkDuration"));
                    } else {
                        System.out.println("  Walk Duration: N/A");
                    }
                }
                case "http://www.iiius.de/ontologies/public_transit_travel_information_ontology#InterchangeLeg" -> {
                    if (soln.contains("interchangeDuration")) {
                        System.out.println("  Interchange Duration: " + soln.get("interchangeDuration"));
                    } else {
                        System.out.println("  Interchange Duration: N/A");
                    }
                }
                case "http://www.iiius.de/ontologies/public_transit_travel_information_ontology#TimedLeg" -> {
                    if (soln.contains("line")) {
                        System.out.println("  Line: " + soln.get("line"));
                    } else {
                        System.out.println("  Line: N/A");
                    }
                    if (soln.contains("transitMode")) {
                        System.out.println("  Transit Mode: " + soln.get("transitMode"));
                    } else {
                        System.out.println("  Transit Mode: N/A");
                    }
                    if (soln.contains("realtimeData")) {
                        System.out.println("  Realtime Data: " + soln.get("realtimeData"));
                    } else {
                        System.out.println("  Realtime Data: N/A");
                    }
                    System.out.println("  Board:");
                    if (soln.contains("scheduledTimeBoard")) {
                        System.out.println("    Scheduled Time: " + soln.get("scheduledTimeBoard"));
                    } else {
                        System.out.println("    Scheduled Time: N/A");
                    }
                    if (soln.contains("plannedBayBoard")) {
                        System.out.println("    Planned Bay: " + soln.get("plannedBayBoard"));
                    } else {
                        System.out.println("    Planned Bay: N/A");
                    }
                    if (soln.contains("stopSequenceBoard")) {
                        System.out.println("    Stop Sequence: " + soln.get("stopSequenceBoard"));
                    } else {
                        System.out.println("    Stop Sequence: N/A");
                    }
                    System.out.println("  Alight:");
                    if (soln.contains("scheduledTimeAlight")) {
                        System.out.println("    Scheduled Time: " + soln.get("scheduledTimeAlight"));
                    } else {
                        System.out.println("    Scheduled Time: N/A");
                    }
                    if (soln.contains("plannedBayAlight")) {
                        System.out.println("    Planned Bay: " + soln.get("plannedBayAlight"));
                    } else {
                        System.out.println("    Planned Bay: N/A");
                    }
                    if (soln.contains("stopSequenceAlight")) {
                        System.out.println("    Stop Sequence: " + soln.get("stopSequenceAlight"));
                    } else {
                        System.out.println("    Stop Sequence: N/A");
                    }
                    if (soln.contains("infos")) {
                        System.out.println("  Infos: " + soln.get("infos"));
                    } else {
                        System.out.println("  Infos: N/A");
                    }
                }
                default -> System.out.println("  Leg type: Unknown");
            }
            System.out.println("----------------------------------------------------------------");
        }
    }
}
