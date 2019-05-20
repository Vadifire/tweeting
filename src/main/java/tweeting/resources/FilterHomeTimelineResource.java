package tweeting.resources;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/1.0/tweet/filter")
@Produces(MediaType.APPLICATION_JSON)

public class FilterHomeTimelineResource {

    private static final Logger logger = LoggerFactory.getLogger(FilterHomeTimelineResource.class);

    public static final String FILTER_PARAM = "keyword";

    public static final String MISSING_FILTER_MESSAGE = "\"" + FILTER_PARAM + "\" parameter is required to filter" +
            " home timeline.";

    private TwitterService service;

    public FilterHomeTimelineResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/tweet/filter?keyword=KEYWORD
     *
     * Replace 'KEYWORD' with desired keyword to filter by. Replace HOST and PORT with configured values
     */
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getHomeTimeline(@QueryParam(FILTER_PARAM) String keyword) {
        try {
            if (keyword != null) {
                List<Tweet> tweets = service.getHomeTimeline().stream()
                        .filter(t -> t.getMessage().isPresent())
                        .filter(t -> StringUtils.containsIgnoreCase(t.getMessage().get(), keyword))
                        .collect(Collectors.toList());

                logger.info("Successfully retrieved home timeline from Twitter. Sending 200 OK response.");
                return Response.ok(tweets)
                        .build();
            } else {
                logger.debug("\"" + FILTER_PARAM + "\" parameter required for filtering is missing. " +
                        "Sending 400 Bad Request error");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(MISSING_FILTER_MESSAGE)
                        .build();
            }
        } catch (TwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(TwitterService.SERVICE_UNAVAILABLE_MESSAGE)
                    .build());
        }
    }

}