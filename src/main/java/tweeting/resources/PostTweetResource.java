package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.services.BadTwitterServiceCallException;
import tweeting.services.BadTwitterServiceResponseException;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import twitter4j.Status;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class PostTweetResource {

    /* Constants */
    private static final Logger logger = LoggerFactory.getLogger(PostTweetResource.class);

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
    public Response postTweet(@FormParam("message") String message) { // Receives message from JSON data
        try {
            final Tweet returnedStatus = service.postTweet(message); // Status should be updated to message
            logger.info("Successfully posted '{}' to Twitter. Sending 201 Created response.", message);
            // Return successful response with returned status
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            responseBuilder.type(MediaType.APPLICATION_JSON);
            Response response = responseBuilder.entity(returnedStatus).build();
            return response;
        } catch (BadTwitterServiceCallException e) {
            logger.debug("Sending 400 Bad Request error", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (BadTwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage())).build();
        }
    }

}