package main.resources;

import main.twitter.TwitterAPIWrapper;
import main.twitter.TwitterErrorCode;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {

    private TwitterAPIWrapper api;

    public GetTimelineResource(TwitterAPIWrapper api) {
        this.api = api;
    }

    /*
     * How to use:
     * curl -i -X GET http://localhost:8080/api/1.0/twitter/timeline
     */
    @GET
    public Response getTweets() {
        try {
            List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) { //this might never actually return true
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Failed to retrieve home timeline from Twitter.").build();
            }
            return Response.ok(statuses).build();

        } catch (TwitterException e) {

            if (e.getErrorCode() == TwitterErrorCode.AUTH_FAIL.getValue()) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline because service is temporarily unavailable.").
                            build();

            } else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline because connection to Twitter failed.").build();
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline: " + e.getErrorMessage()).build();
            }
        }
    }

}