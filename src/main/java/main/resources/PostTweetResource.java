package main.resources;

import com.codahale.metrics.annotation.Timed;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

@Path("/api/1.0/twitter/tweet/{message}")
@Consumes(MediaType.APPLICATION_JSON)
public class PostTweetResource {

	private final String template;
	private final String defaultMessage;
	private final AtomicLong counter; //provides thread-safe unique ID

	public PostTweetResource(String template, String defaultMessage) {
		this.template = template;
		this.defaultMessage = defaultMessage;
		this.counter = new AtomicLong();
	}

    /*
     * How to use:
     * curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/api/1.0/twitter/tweet/message
     *
     * Replace 'message' with message desired.
     */
	@POST
	@Timed
	public Response postTweet(@PathParam("message") String message) { // Change to return HTTP response?
        if (message == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was null.").build();
        }
        if (message.length() > 280) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was over the 280 character limit.").build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: message was 0 characters.").build();
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(message);
        } catch (TwitterException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with posting tweet: "+e.getErrorMessage()).build();
        }
        return Response.status(Response.Status.OK).entity("Successfully posted tweet: "+message).build();
	}
}