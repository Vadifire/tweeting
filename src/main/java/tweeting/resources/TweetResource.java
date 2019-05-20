package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class TweetResource {

    /* Constants */
    private static final Logger logger = LoggerFactory.getLogger(TweetResource.class);

    private TwitterService service;

    public TweetResource(TwitterService service) {
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response postTweet(@FormParam("message") String message) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(service.postTweet(message)
                            .orElseThrow(() -> new NullPointerException("Twitter failed to respond posted tweet.")))
                    .build();
        } catch (TwitterServiceCallException e) {
            logger.debug("Sending 400 Bad Request error", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
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