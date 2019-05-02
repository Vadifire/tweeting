package main.resources;

import main.twitter.TwitterAPIWrapper;
import main.twitter.TwitterAPIWrapperImpl;
import main.twitter.TwitterErrorCode;

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
                    entity("Could not post tweet because no message was specified.").build();
        }
        if (message.length() > 280) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Could not post tweet because message exceeds 280 character limit.").build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Could not post tweet because message was empty.").build();
        }

        try {
            api.updateStatus(message);
        } catch (TwitterException e) {

            if (e.getErrorCode() == TwitterErrorCode.AUTH_FAIL.getValue()) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because service is temporarily unavailable.").build();
            }
            else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because connection to Twitter failed.").build();
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet: " + e.getErrorMessage()).build();
            }
        }
        return Response.status(Response.Status.CREATED).entity("Successfully posted tweet: " + message).build();
	}
}