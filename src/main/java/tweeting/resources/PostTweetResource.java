package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class PostTweetResource {

    private static final Logger logger = LoggerFactory.getLogger(PostTweetResource.class);

    public static final String ATTEMPTED_ACTION = "post tweet";
    public static final String PARAM_UNIT = "characters";
    public static final String MESSAGE_PARAM = "message";

    private TwitterService service;

    public PostTweetResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i http://HOST:PORT/api/1.0/twitter/tweet -d 'message=Hello World'
     *
     * Replace 'Hello World' with desired message, replace HOST and PORT with configured values
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postTweet(@FormParam(MESSAGE_PARAM) String message) { // Receives message from JSON data
        try {
            return service.postTweet(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage(ATTEMPTED_ACTION))).build();
        }
    }

}