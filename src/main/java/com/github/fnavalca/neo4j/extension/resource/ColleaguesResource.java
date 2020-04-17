package com.github.fnavalca.neo4j.extension.resource;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

@Path("/colleagues")
public class ColleaguesResource {
    private GraphDatabaseService graphDb;
    private final ObjectMapper objectMapper;

    private static final RelationshipType ACTED_IN = RelationshipType.withName("ACTED_IN");
    private static final Label PERSON = Label.label("Person");

    public ColleaguesResource(@Context GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Path("/{personName}")
    public Response findColleagues(@PathParam("personName") final String personName) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                JsonGenerator jg = objectMapper.getJsonFactory().createJsonGenerator(os, JsonEncoding.UTF8);
                jg.writeStartObject();
                jg.writeFieldName("colleagues");
                jg.writeStartArray();

                try (Transaction tx = graphDb.beginTx();
                     ResourceIterator<Node> persons = graphDb.findNodes(PERSON, "name", personName)) {
                    while (persons.hasNext()) {
                        Node person = persons.next();
                        for (Relationship actedIn : person.getRelationships(ACTED_IN, OUTGOING)) {
                            Node endNode = actedIn.getEndNode();
                            for (Relationship colleagueActedIn : endNode.getRelationships(ACTED_IN, INCOMING)) {
                                Node colleague = colleagueActedIn.getStartNode();
                                if (!colleague.equals(person)) {
                                    jg.writeString(colleague.getProperty("name").toString());
                                }
                            }
                        }
                    }
                    tx.success();
                }

                jg.writeEndArray();
                jg.writeEndObject();
                jg.flush();
                jg.close();
            }
        };

        return Response.ok().entity(stream).type(MediaType.APPLICATION_JSON).build();
    }
}
