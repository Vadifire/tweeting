package main.resources;

//TODO check imports
import main.ProgramOne;
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

import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

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
     * How to use: //TODO: make this more readable
     * curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/api/1.0/twitter/tweet/message
     *
     * Replace 'message' with message desired.
     */
	@POST
	@Timed
	public boolean postTweet(@PathParam("message") String message) { // Change to return HTTP response?
		return updateStatus(message);
	}

    /*
     * Returns true iff successfully posted Status update
     */
    public static boolean updateStatus(String updateText) {
        if (updateText == null) {
            System.out.println("Could not update status to null String");
            return false;
        }
        if (updateText.length() > 280) {
            System.out.println("Could not update status to String over 280 characters in length.");
            return false;
        }
        if (updateText.length() == 0) {
            System.out.println("Could not update status to 0 length String.");
            return false;
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(updateText);
        } catch (TwitterException e) {
            e.printStackTrace();
            System.out.println("Could not update status because connection to Twitter API failed.");
            return false;
        }
        return true;
    }
}