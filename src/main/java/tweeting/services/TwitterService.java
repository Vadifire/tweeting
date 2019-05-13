package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.TweetingApplication;
import tweeting.resources.GetTimelineResource;
import tweeting.resources.PostTweetResource;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterErrorCode;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.util.CharacterUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/*
 * Provides Twitter4J's functionality as a service
 */
public class TwitterService {

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    private Twitter api; // API for Twitter4J

    private static TwitterService instance;

    private TwitterService() {
    }

    // 'Double Checked Locking' to make getInstance() thread safe
    public static TwitterService getInstance() {
        if (instance == null) {
            synchronized (TwitterService.class) {
                if (instance == null) {
                    instance = new TwitterService();
                }
            }
        }
        return instance;
    }
    
    // API must be set to make calls to Twitter4J
    public void setTwitterAPI(Twitter api) {
        this.api = api;
    }

    public Response postTweet(String message) {
        try {
            if (message == null) {
                logger.debug("Request is missing message parameter. Sending 400 Bad Request error.");
                return Response.status(Response.Status.BAD_REQUEST).
                        entity(ResponseUtil.getNullParamErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                                PostTweetResource.MESSAGE_PARAM)).build();
            }
            if (message.length() > CharacterUtil.MAX_TWEET_LENGTH || StringUtils.isBlank(message)) {
                logger.debug("Message parameter is blank or over the {} character limit. Sending 400 Bad Request " +
                        "error.", CharacterUtil.MAX_TWEET_LENGTH);
                return Response.status(Response.Status.BAD_REQUEST).
                        entity(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                                PostTweetResource.MESSAGE_PARAM,
                                PostTweetResource.PARAM_UNIT, CharacterUtil.MAX_TWEET_LENGTH)).build();
            }
            Status returnedStatus = api.updateStatus(message); // Status should be updated to message
            logger.info("Successfully posted '{}' to Twitter. Sending 201 Created response.", message);
            // Return successful response with returned status
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            responseBuilder.type(MediaType.APPLICATION_JSON);
            Response response = responseBuilder.entity(returnedStatus).build();
            return response;

        } catch (TwitterException e) {
            return catchTwitterException(e, PostTweetResource.ATTEMPTED_ACTION);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage(PostTweetResource.ATTEMPTED_ACTION))).build();
        }
    }

    public Response getHomeTimeline() {
        try {
            List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) {
                logger.warn("Twitter failed to respond with a valid home timeline. " +
                        "Sending 500 Internal Server Error.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getNullResponseErrorMessage(GetTimelineResource.ATTEMPTED_ACTION)).build();
            }
            logger.info("Successfully retrieved home timeline from Twitter. Sending 200 OK response.");
            return Response.ok(statuses).build(); // Successfully got timeline

        } catch (TwitterException e) {
            return catchTwitterException(e, GetTimelineResource.ATTEMPTED_ACTION);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage(GetTimelineResource.ATTEMPTED_ACTION))).build();
        }
    }

    /*
     * Catches the TwitterExceptions classes and returns appropriate Response
     */
    public Response catchTwitterException(TwitterException exception, String attemptedAction) {
        try {
            if (exception.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                    exception.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
                logger.error("Twitter authentication failed. Please restart server with valid Twitter credentials." +
                                " Twitter credentials can be generated or retrieved here: " +
                                " https://developer.twitter.com/en/apps. Configuration file used for credentials: {}",
                        TweetingApplication.getConfigFileName(), exception);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction)).build();

            } else if (exception.isCausedByNetworkIssue()) {
                logger.error("Connection to Twitter failed.", exception);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getNetworkErrorMessage(attemptedAction)).build();
            } else { // 'Other' fail-safe
                logger.error("Request to Twitter failed. Error code: {} Error message: \"{}\"",
                        exception.getErrorCode(), exception.getMessage(), exception);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(ResponseUtil.getOtherErrorMessage(attemptedAction, exception.getErrorMessage())).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return (Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction))).build();
        }
    }
}
