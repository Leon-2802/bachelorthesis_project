## Project description
 
This project is part of my bachelor thesis "Conception and prototypical realisation of semantic models for data processing in distributed data structures of public transport systems".

The project aims to visualize the use of semantic models in processing distributed data sources in public transport systems. 
In this particular case, the user shall be able to recieve all available data concerning a given connection from one of the two data sources [1], without having to deal with sending queries to the API or GTFS-dataset of the respective transport association.
Independent from the underlying data source, the query for a connection-information-request always follows the same format. This also counts for the data-structure of the response. 
This is achieved by the usage of semantic annotations, which insert the heterogenous data into the structure of the semantic model.

The implementation code is found under: SemanticModelPrototype/src/main/java/org/implementation
For viewing the RDF-file of the semantic model, open the folder "protege"


Framework: Apache Jena 5.10
JDK: 17 Oracle OpenJDK version 17

#### Run Executable
If you would like to run the .jar executable, open the repository in an IDE, preferably IntelliJ.
Then go to out/artifacts/bachelorthesis_project_jar, right-click the file bachelorthesis_project.jar and choose "Run" from the pop-up menu.

#### Run Source Code: 
1. Download following binary distribution of the framework: https://dlcdn.apache.org/jena/binaries/apache-jena-5.1.0.tar.gz
2. Extract the compressed folder
3. Import all the .jar files found inside the lib folder of the extracted source folder into the repo, by opening it in IntelliJ (or other preffered IDE) and adding it under the libraries-tab in the project settings (see screenshot below)

![Screenshot 2025-01-15 175928](https://github.com/user-attachments/assets/b7483bbb-e85b-4a51-b752-c33884ebe69d)



[1] https://www.vrn.de/opendata/API; https://transport.data.gouv.fr/datasets/fr-200052264-t0049-0000-1 
