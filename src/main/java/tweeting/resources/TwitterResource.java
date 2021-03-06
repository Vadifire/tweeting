package tweeting.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/")
@Singleton
public class TwitterResource {

    private static final Logger logger = LoggerFactory.getLogger(TwitterResource.class);

    private TwitterService service;

    @Inject
    public TwitterResource(TwitterService service) {
        this.service = service;
    }

    /*
     * How to use:
     * curl -i http://HOST:PORT/api/1.0/twitter/tweet -d 'message=Hello World'
     *
     * Replace 'Hello World' with desired message, replace HOST and PORT with configured values
     */
    @Path("tweet/")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postTweet(@FormParam("message") String message) {
        try {
            return service.postTweet(message)
                    .map(tweet -> Response.status(Response.Status.CREATED)
                            .entity(tweet)
                            .build())
                    .get();
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
                    .entity(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                    .build();
        }
    }

    @Path("tweet/reply")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyToTweet(@FormParam("parentId") Long parentId, @FormParam("message") String message) {
        try {
            return service.replyToTweet(parentId, message)
                    .map(tweet -> Response.status(Response.Status.CREATED)
                            .entity(tweet)
                            .build())
                    .get();
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
                    .entity(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                    .build();
        }
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline
     *
     * Replace HOST and PORT with configured values
     */
    @Path("timeline")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHomeTimeline() {
        try {
            return service.getHomeTimeline()
                    .map(timeline -> Response.ok(timeline)
                            .build())
                    .get();
        } catch (TwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                    .build();
        }
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline/user
     *
     * Replace HOST and PORT with configured values
     */
    @Path("timeline/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTimeline() {
        try {
            return service.getUserTimeline()
                    .map(timeline -> Response.ok(timeline)
                            .build())
                    .get();
        } catch (TwitterServiceResponseException e) {
            logger.error("Sending 500 Internal Server error", e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE))
                    .build();
        }
    }

    /*
     * How to use:
     * curl -i -X GET http://HOST:PORT/api/1.0/twitter/timeline/filter?keyword=KEYWORD
     *
     * Replace 'KEYWORD' with desired keyword to filter by. Replace HOST and PORT with configured values
     */
    @Path("timeline/filter")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilteredHomeTimeline(@QueryParam("keyword") String keyword) {
        try {
            return service.getFilteredTimeline(keyword)
                    .map(filteredTweets -> Response.ok(filteredTweets)
                            .build())
                    .get();
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
                    .entity(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE)
                    .build());
        }
    }

}