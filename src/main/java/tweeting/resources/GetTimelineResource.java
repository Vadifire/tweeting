package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {

    public static final String ATTEMPTED_ACTION = "retrieve home timeline";

    TwitterService service;
    private static final Logger logger = LoggerFactory.getLogger(GetTimelineResource.class);

    public GetTimelineResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline
     *
     * Replace HOST and PORT with configured values
     */
    @GET
    public Response getHomeTimeline() {
        try {
            return service.getHomeTimeline();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    ResponseUtil.getServiceUnavailableErrorMessage(ATTEMPTED_ACTION))).build();
        }
    }

}