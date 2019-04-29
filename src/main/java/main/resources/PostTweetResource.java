package main.resources;

import com.codahale.metrics.annotation.Timed;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.NullAuthorization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
@Consumes(MediaType.APPLICATION_JSON)
public class PostTweetResource {
    /*
     * How to use:
     * curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/api/1.0/twitter/tweet/message
     *
     * Replace 'message' with message desired.
     */
	@POST
	@Timed
	public Response postTweet(@QueryParam("message") String message) { // Change to return HTTP response?
        if (message == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was null. Add message as a post parameter by " +
                            "appending ?message= at the end of URI.\n").build();
        }
        if (message.length() > 280) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was over the 280 character limit.\n").build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was 0 characters.\n").build();
        }
        Twitter twitter = TwitterFactory.getSingleton();
        if (twitter.getAuthorization().getClass() == NullAuthorization.class) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: authentication failed. See " +
                            "http://twitter4j.org/en/configuration.html for help setting up authentication.\n").build();
        }
        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: "+e.getErrorMessage()+"\n").build();
        }
        return Response.status(Response.Status.CREATED).entity("Successfully posted tweet: "+message+"\n").build();
	}
}