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
                    "\n" +
                    "SELECT ?trip ?legType ?walkDuration ?originStopID ?originStopName ?destinationStopID ?destinationStopName ?line ?transitMode ?scheduledTime ?plannedBay ?stopSequence ?info\n" +
                    "WHERE {\n" +
                    "  ?trip rdf:type j0:Trip ;\n" +
                    "        j0:hasTripLeg ?leg .\n" +
                    "  \n" +
                    "  ?leg rdf:type ?legType .\n" +
                    "  \n" +
                    "  OPTIONAL { ?leg j0:hasWalkDuration ?walkDuration . }\n" +
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
                    "  }\n" +
                    "  OPTIONAL { \n" +
                    "    ?leg j0:hasBoard ?board .\n" +
                    "    ?board j0:hasScheduledTime ?scheduledTime ;\n" +
                    "           j0:hasPlannedBay ?plannedBay ;\n" +
                    "           j0:hasStopSequence ?stopSequence .\n" +
                    "  }\n" +
                    "  OPTIONAL { ?leg j0:hasInformation ?info . }\n" +
                    "}\n" +
                    "ORDER BY ?trip ?legType ?scheduledTime\n";

        // Create and execute the query
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                System.out.println("Trip: " + soln.get("trip"));
                System.out.println("  Interchanges: " + soln.get("hasInterchanges"));
                System.out.println("  End Time: " + soln.get("hasEndTime"));
                System.out.println("  Start Time: " + soln.get("hasStartTime"));
                System.out.println("  Duration: " + soln.get("hasDuration"));
            }
        }
    }
}
