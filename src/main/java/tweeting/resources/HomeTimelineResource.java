package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
public class HomeTimelineResource {

    private static final Logger logger = LoggerFactory.getLogger(HomeTimelineResource.class);

    private TwitterService service;

    public HomeTimelineResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline
     *
     * Replace HOST and PORT with configured values
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHomeTimeline() {
        try {
            return service.getHomeTimeline()
                    .map(timeline -> Response.ok(timeline)
                            .build())
                    .orElseThrow(() -> new NullPointerException("Twitter failed to respond with home timeline."));
        } catch (TwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(TwitterService.SERVICE_UNAVAILABLE_MESSAGE))
                    .build();
        }
    }

}