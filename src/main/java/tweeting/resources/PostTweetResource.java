package tweeting.resources;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.util.CharacterUtil;

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

    private Twitter api;

    private TwitterExceptionHandler exceptionHandler;

    public PostTweetResource(Twitter api) {
        this.api = api;
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
            if (message.length() > CharacterUtil.MAX_TWEET_LENGTH || StringUtils.isBlank(message)) {
                logger.debug("Message parameter is blank or over the {} character limit. Sending 400 Bad Request " +
                        "error.", CharacterUtil.MAX_TWEET_LENGTH);
                return Response.status(Response.Status.BAD_REQUEST).
                        entity(ResponseUtil.getParamBadLengthErrorMessage(ATTEMPTED_ACTION, MESSAGE_PARAM,
                                PARAM_UNIT, CharacterUtil.MAX_TWEET_LENGTH)).build();
            }
            Status returnedStatus = api.updateStatus(message); // Status should be updated to message
            logger.info("Successfully posted '{}' to Twitter. Sending 201 Created response.", message);
            // Return successful response with returned status
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            responseBuilder.type(MediaType.APPLICATION_JSON);
            Response response = responseBuilder.entity(returnedStatus).build();
            return response;

        } catch (TwitterException e) {
            logger.warn("Encountered Twitter Exception while attempting to {}. Error is being handled by {} class.",
                    ATTEMPTED_ACTION, exceptionHandler.getClass().getName());
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