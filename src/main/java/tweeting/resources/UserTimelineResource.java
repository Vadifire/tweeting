package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceResponseException;
import tweeting.util.ResponseUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/1.0/tweet/filter")
@Produces(MediaType.APPLICATION_JSON)

public class UserTimelineResource {

    private static final Logger logger = LoggerFactory.getLogger(UserTimelineResource.class);

    private TwitterService service;

    public UserTimelineResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/tweet/filter
     *
     * Replace HOST and PORT with configured values
     */
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getUserTimeline(@QueryParam("text") Optional<String> filter) {
        try {
            final List<Tweet> tweets = service.getUserTimeline();

            final List<Tweet> filteredTweets = tweets.stream()
                    .filter(t -> t.getMessage().contains(filter.get()))
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved home timeline from Twitter. Sending 200 OK response.");
            return Response.ok(filteredTweets).build(); // Successfully got timeline
        } catch (TwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage())).build();
        }
    }

}