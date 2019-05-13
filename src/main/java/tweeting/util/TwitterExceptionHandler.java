package tweeting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.TweetingApplication;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

public class TwitterExceptionHandler {

    private String attemptedAction;
    private final Logger logger = LoggerFactory.getLogger(TwitterExceptionHandler.class);

    public TwitterExceptionHandler(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }

    /*
     * Catches the TwitterException in resource classes and returns appropriate Response
     */
    public Response catchTwitterException(TwitterException exception) {
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
