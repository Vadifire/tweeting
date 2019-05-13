package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import twitter4j.Status;
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

    private static final Logger logger = LoggerFactory.getLogger(GetTimelineResource.class);

    /* Constants */
    public static final String ATTEMPTED_ACTION = "retrieve home timeline";

    private TwitterService service;

    private TwitterExceptionHandler exceptionHandler;

    public GetTimelineResource(TwitterService service) {
        this.service = service;
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
            final List<Status> statuses = service.getTweets();
            if (statuses == null) {
                logger.warn("Twitter failed to respond with a valid home timeline. " +
                        "Sending 500 Internal Server Error.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getNullResponseErrorMessage(ATTEMPTED_ACTION)).build();
            }
            logger.info("Successfully retrieved home timeline from Twitter. Sending 200 OK response.");
            return Response.ok(statuses).build(); // Successfully got timeline

        } catch (TwitterException e) {
            return exceptionHandler.catchTwitterException(e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage(ATTEMPTED_ACTION))).build();
        }
    }

    /*
     * Used for mocking purposes
     */
    public void setExceptionHandler(TwitterExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}