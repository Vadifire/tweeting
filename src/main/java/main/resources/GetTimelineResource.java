package main.resources;

//TODO check imports
import main.api.Timeline;
import main.ProgramTwo;
import com.codahale.metrics.annotation.Timed;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import java.util.List;
import java.util.LinkedList;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {
	private final AtomicLong counter; //Provides thread-safe unique(ish) ID

    public GetTimelineResource() {
        this.counter = new AtomicLong();
    }

	/*
	 * How to use: //TODO: make this more readable
	 * curl -i -X GET http://localhost:8080/api/1.0/twitter/timeline
	 */
    @GET
    @Timed
    public Timeline getTweets() { //TODO: add error checking
    	List<String> value = new LinkedList<>();
    	List<Status> statuses = getHomeTimelineStatuses();
    	if (statuses == null){
    		//TODO: exception
    	}
    	for (Status status : statuses) {
    		value.add(status.getText());
    	}
    	return new Timeline(counter.incrementAndGet(), value);
    }

	/*
	 * Returns List of Status objects from Home Timeline
	 * Returns null if TwitterException occurs when trying to retrieve statuses
	 */
	public static List<Status> getHomeTimelineStatuses() {
		Twitter twitter = TwitterFactory.getSingleton();
		try {
			List<Status> statuses = twitter.getHomeTimeline();
			return statuses;
		} catch (TwitterException e) {
			e.printStackTrace();
			return null;
		}
	}

}