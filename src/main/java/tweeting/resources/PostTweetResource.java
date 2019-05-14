package tweeting.resources;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import tweeting.util.TwitterServiceException;
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
    public static final String MESSAGE_PARAM = "message"; // Used in ResponseUtil
    public static final String ATTEMPTED_ACTION = "post tweet";
    public static final String PARAM_UNIT = "characters";
    private static final Logger logger = LoggerFactory.getLogger(PostTweetResource.class);

    private TwitterService service;

    private TwitterExceptionHandler exceptionHandler;

    public PostTweetResource(TwitterService service) {
        this.service = service;
        setExceptionHandler(new TwitterExceptionHandler(ATTEMPTED_ACTION));
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
            if (message == null) {
                logger.debug("Request is missing message parameter. Sending 400 Bad Request error.");
                return Response.status(Response.Status.BAD_REQUEST).
                        entity(ResponseUtil.getNullParamErrorMessage(ATTEMPTED_ACTION, MESSAGE_PARAM)).build();
            }
            if (message.length() > service.getMaxCharacterLength() || StringUtils.isBlank(message)) {
                logger.debug("Message parameter is blank or over the {} character limit. Sending 400 Bad Request " +
                        "error.", service.getMaxCharacterLength());
                return Response.status(Response.Status.BAD_REQUEST).
                        entity(ResponseUtil.getParamBadLengthErrorMessage(ATTEMPTED_ACTION, MESSAGE_PARAM,
                                PARAM_UNIT, service.getMaxCharacterLength())).build();
            }
            final Status returnedStatus = service.postTweet(message); // Status should be updated to message
            logger.info("Successfully posted '{}' to Twitter. Sending 201 Created response.", message);
            // Return successful response with returned status
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            responseBuilder.type(MediaType.APPLICATION_JSON);
            Response response = responseBuilder.entity(returnedStatus).build();
            return response;

        } catch (TwitterServiceException e) {
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