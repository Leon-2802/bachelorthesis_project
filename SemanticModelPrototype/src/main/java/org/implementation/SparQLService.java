package org.implementation;

import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.query.*;

public final class SparQLService {

    private SparQLService( ) {}

    public static void sendQuery(OntModel model) {
        String queryString =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX j0: <" + Constants.ontRoot + "> " + // Change to your ontology's namespace
            "SELECT ?trip ?hasInterchanges ?hasEndTime ?hasStartTime ?hasDuration " +
            "WHERE { " +
            "  ?trip rdf:type j0:Trip . " +
            "  ?trip j0:hasInterchanges ?hasInterchanges . " +
            "  ?trip j0:hasEndTime ?hasEndTime . " +
            "  ?trip j0:hasStartTime ?hasStartTime . " +
            "  ?trip j0:hasDuration ?hasDuration . " +
            "}";

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
