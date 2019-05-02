package main.resources;

import main.twitter.TwitterAPIWrapper;

import twitter4j.TwitterException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class PostTweetResource {

    private TwitterAPIWrapper api;

    public PostTweetResource(TwitterAPIWrapper api) {
        this.api = api;
    }

    /*
     * Separate response messages to enum to facilitate correct response message unit testing
     */
    public enum ResponseMessage {

        NULL_MESSAGE("Could not post tweet because no message was specified."),
        TOO_LONG_MESSAGE("Could not post tweet because message exceeds 280 character limit."),
        TOO_SHORT_MESSAGE("Could not post tweet because message was empty."),
        AUTH_FAIL("Could not post tweet because service is temporarily unavailable."),
        NETWORK_ISSUE("Could not post tweet because connection to Twitter failed.");

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
     * curl -i http://localhost:8080/api/1.0/twitter/tweet -d 'message=Hello World'
     *
     * Replace 'Hello World' with desired message.
     */
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postTweet(@FormParam("message") String message) { // Receives message from JSON data
        if (message == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ResponseMessage.NULL_MESSAGE.getValue()).build();
        }
        if (message.length() > 280) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ResponseMessage.TOO_LONG_MESSAGE.getValue()).build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ResponseMessage.TOO_SHORT_MESSAGE.getValue()).build();
        }

        try {
            api.updateStatus(message);
        } catch (TwitterException e) {

            if (e.getStatusCode() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseMessage.AUTH_FAIL.getValue()).build();
            }
            else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseMessage.NETWORK_ISSUE.getValue()).build();
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet: " + e.getErrorMessage()).build();
            }
        }
        return Response.status(Response.Status.CREATED).entity("Successfully posted tweet: " + message).build();
	}
}