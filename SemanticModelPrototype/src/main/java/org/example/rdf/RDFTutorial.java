package org.example.rdf;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.VCARD;

// Each arc in an RDF Model is called a statement -> subject, property, object (johnSmith, FirstName, John)

public class RDFTutorial {
    public static void printModel(Model model) {
        // print out smaller models in a pretty way:
        RDFDataMgr.write(System.out, model, Lang.RDFXML);
        // for larger models:
        RDFDataMgr.write(System.out, model, Lang.NTRIPLES);
    }

    public static void navigateModel(Model model) {
        Resource vcard = model.getResource("http://somewhere/JohnSmith");
        Resource name = vcard.getProperty(VCARD.N).getResource();
        String fullName = vcard.getProperty(VCARD.FN).getString();
        System.out.println("Name: " + fullName);

        vcard.addProperty(VCARD.NICKNAME, "Smithy")
                .addProperty(VCARD.NICKNAME, "Johnny");
        // set up the output
        System.out.println("The nicknames of \""
                + fullName + "\" are:");
        // list the nicknames -> !If a property occurs more than once, you have to list it, bc otherwise it is random, which of the property values is returned
        StmtIterator iter = vcard.listProperties(VCARD.NICKNAME);
        while (iter.hasNext()) {
            System.out.println("    " + iter.nextStatement()
                    .getObject()
                    .toString());
        }
    }

    public static void queryModel(Model model) {
        // Jena API only supports a limited query primitive -> for more powerful query facilities use SPARQL
        ResIterator iter = model.listSubjectsWithProperty(VCARD.FN); // -> find all vcards, bc all resources have an FN property
        if (iter.hasNext()) {
            System.out.println("The database contains vcards for:");
            while (iter.hasNext()) {
                System.out.println("  " + iter.nextResource()
                        .getProperty(VCARD.FN)
                        .getString());
            }
        } else {
            System.out.println("No vcards were found in the database");
        }
    }

    public static void operationsOnModel(Model model1, Model model2) {
        Model modelDiff = model2.difference(model1);
        Model modelInters = model2.intersection(model1);
        Model modelUnion = model1.union(model2);
        modelUnion.write(System.out, "RDF/XML-ABBREV");
    }

    public static void containers(Model model) {
        // create a bag (! other container types: ALT - unordered collection to represent alternatives; SEQ for ordered collections)
        Bag smiths = model.createBag();
        // select all the resources with a VCARD.FN property
        // whose value ends with "Smith"
        ResIterator resIter = model.listSubjectsWithProperty(VCARD.FN);
        // add the Smith's to the bag
        while (resIter.hasNext()) {
            Resource res = resIter.nextResource();
            Statement stmt = res.getProperty(VCARD.FN);
            if (stmt != null && stmt.getString().endsWith("Smith")) {
                smiths.add(stmt.getSubject());
            }
        }

        // print out the members of the bag
        NodeIterator nodeIter = smiths.iterator();
        if (nodeIter.hasNext()) {
            System.out.println("The bag contains:");
            while (nodeIter.hasNext()) {
                System.out.println("  " +
                        ((Resource) nodeIter.next())
                                .getProperty(VCARD.FN)
                                .getString());
            }
        } else {
            System.out.println("The bag is empty");
        }
    }
}
