package main.resources;

import main.api.Timeline;
import main.api.Message;
import com.codahale.metrics.annotation.Timed;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.NullAuthorization;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import java.util.List;
import java.util.LinkedList;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {
    //private final AtomicLong counter; //Provides thread-safe unique(ish) ID

    public GetTimelineResource() {
        //this.counter = new AtomicLong();
    }

    /*
     * How to use:
     * curl -i -X GET http://localhost:8080/api/1.0/twitter/timeline
     */
    @GET
    @Timed
    public Response getTweets() {

        Twitter twitter = TwitterFactory.getSingleton();
        if (twitter.getAuthorization().getClass() == NullAuthorization.class){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with retrieving home timeline: authentication failed. See " +
                            "http://twitter4j.org/en/configuration.html for help setting up authentication.\n").build();
        }
        try {
            //Retrieve Statuses using Twitter4J
            List<Status> statuses = twitter.getHomeTimeline();

            if (statuses == null){ //this might never actually return true
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity("Error with retrieving home timeline: home timeline was null.\n").build();
            }

            // Build timeline Response from Status List
            List<Message> timelineValue = new LinkedList<>();
            for (Status status : statuses) {
                Message m = new Message(status.getId(), status.getUser().getName(), status.getText());
                timelineValue.add(m);
            }
            Timeline timeline = new Timeline(timelineValue);
            return Response.ok(timeline).build();

        } catch (TwitterException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity("Error with retrieving home timeline: "+e.getErrorMessage()+"\n").build();
        }
    }

}