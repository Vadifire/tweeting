package main.resources;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class PostTweetResource {
    /*
     * How to use:
     * curl http://localhost:8080/api/1.0/twitter/tweet -H 'Content-type:text/plain' -d 'Hello World'
     *
     * Replace 'Hello World' with desired message.
     */
	@POST
    @Consumes(MediaType.TEXT_PLAIN)
	public Response postTweet(String message) { // Receives message from JSON data
        if (message == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Could not post tweet because no message was specified.\n").build();
        }
        if (message.length() > 280) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Could not post tweet because message exceeds 280 character limit.\n").build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity("Could not post tweet because message was empty.\n").build();
        }
        Twitter twitter = TwitterFactory.getSingleton();

        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {

            //invalid auth error code (https://developer.twitter.com/en/docs/basics/response-codes.html)
            if (e.getErrorCode() == 32) {
                System.out.println("Twitter authentication failed. Please restart server with " +
                        "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because service is temporarily unavailable.\n").build();
            }
            else if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because connection to Twitter failed.\n").build();
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet: " + e.getErrorMessage() + "\n").build();
            }
        }
        return Response.status(Response.Status.CREATED).entity("Successfully posted tweet: " + message + "\n").build();
	}
}