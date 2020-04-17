package com.github.fnavalca.neo4j.extension.resource;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.string.UTF8;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/helloworld")
public class HelloWorldResource {
    private final GraphDatabaseService database;

    public HelloWorldResource(@Context GraphDatabaseService database) {
        this.database = database;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{nodeId}")
    public Response hello(@PathParam("nodeId") long nodeId) {
        // Do stuff with the database
        return Response.status(Response.Status.OK).entity(UTF8.encode("Hello World, nodeId=" + nodeId)).build();
    }
}
