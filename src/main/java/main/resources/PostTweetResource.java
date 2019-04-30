package main.resources;

import com.codahale.metrics.annotation.Timed;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
@Consumes(MediaType.APPLICATION_JSON)
public class PostTweetResource {
    /*
     * How to use:
     * curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/api/1.0/twitter/tweet/?message=message
     *
     * Replace the last 'message' with desired message.
     */
	@POST
	@Timed
	public Response postTweet(@QueryParam("message") String message) { // Change to return HTTP response?
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
        /*if (!twitter.getAuthorization().isEnabled()) {

            System.out.println("Twitter authentication credentials are not set. Please restart Server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Could not post tweet because because the server is not properly configured.\n").build();
        }*/
        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            e.printStackTrace();
            if (e.isCausedByNetworkIssue()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet because connection to Twitter failed.\n").build();
            } else {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Could not post tweet: " + e.getErrorMessage() + "\n").build();
            }
        }
        return Response.status(Response.Status.CREATED).entity("Successfully posted tweet: " + message + "\n").build();
	}
}