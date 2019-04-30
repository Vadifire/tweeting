package main.resources;

import com.codahale.metrics.annotation.Timed;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {

    /*
     * How to use:
     * curl -i -X GET http://localhost:8080/api/1.0/twitter/timeline
     */
    @GET
    @Timed
    public Response getTweets() {

        Twitter twitter = TwitterFactory.getSingleton();

        try {
            //Retrieve Statuses using Twitter4J
            List<Status> statuses = twitter.getHomeTimeline();

            if (statuses == null) { //this might never actually return true
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Failed to retrieve home timeline from Twitter.\n").build();
            }
            return Response.ok(statuses).build();

        } catch (TwitterException e) {

            //invalid auth error code (https://developer.twitter.com/en/docs/basics/response-codes.html)
            if (e.getErrorCode() == 32) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline because service is temporarily unavailable. " +
                                "\n").build();

            } else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because connection to Twitter failed.\n").build();
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline: " + e.getErrorMessage() + "\n").build();
            }
        }
    }

}