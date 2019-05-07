package tweeting.resources;

import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
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

    /* Constants */
    public static final String ATTEMPTED_ACTION = "retrieve home timeline";

    private Twitter api;

    private TwitterExceptionHandler exceptionHandler;


    public GetTimelineResource(Twitter api) {
        this.api = api;
        setExceptionHandler(new TwitterExceptionHandler(ATTEMPTED_ACTION));
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline
     *
     * Replace HOST and PORT with configured values
     */
    @GET
    public Response getTweets() {

        try {
            List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) { //this might never actually return true
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getNullResponseErrorMessage(ATTEMPTED_ACTION)).build();
            }
            return Response.ok(statuses).build(); // Successfully got timeline

        } catch (TwitterException e) {
            return exceptionHandler.catchTwitterException(e);
        }
    }

    /*
     * Used for mocking purposes
     */
    public void setExceptionHandler(TwitterExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}