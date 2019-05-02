package main.resources;

import twitter4j.Status;
import twitter4j.Twitter;
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

    private Twitter api;

    public GetTimelineResource(Twitter api) {
        this.api = api;
    }

    /*
     * Separate response messages to enum to facilitate correct response message unit testing
     */
    public enum ResponseMessage {

        NULL_TIMELINE("Failed to retrieve home timeline from Twitter."),
        AUTH_FAIL("Could not retrieve home timeline because service is temporarily unavailable."),
        NETWORK_ISSUE("Could not retrieve home timeline because connection to Twitter failed.");

        private final String message;

        ResponseMessage(String message) {
            this.message = message;
        }

        public String getValue() {
            return message;
        }
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
                        entity(ResponseMessage.NULL_TIMELINE.getValue()).build();
            }
            return Response.ok(statuses).build();

        } catch (TwitterException e) {
            if (e.getStatusCode() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseMessage.AUTH_FAIL.getValue()).build();

            } else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseMessage.NETWORK_ISSUE.getValue()).build();
            } else { // 'Other' fail-safe
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not retrieve home timeline: " + e.getErrorMessage()).build();
            }
        }
    }

}