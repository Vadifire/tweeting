package tweeting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;


public class TwitterExceptionHandler {

    private String attemptedAction;
    private final Logger logger = LoggerFactory.getLogger("requestLogger");

    public TwitterExceptionHandler(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }

    /*
     * Catches the TwitterException in resource classes and returns appropriate Response
     */
    public Response catchTwitterException(TwitterException exception) {
        logger.trace("Handling twitter exception...");
        if (exception.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                exception.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            logger.error("Twitter authentication failed. Please restart server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getAuthFailErrorMessage(attemptedAction)).build();

        } else if (exception.isCausedByNetworkIssue()) {
            logger.warn("Connection to Twitter failed.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getNetworkErrorMessage(attemptedAction)).build();
        } else { // 'Other' fail-safe
            logger.warn("Request to Twitter failed. Error code: " + exception.getErrorCode() +
                    "\nError message "+ exception.getMessage() + "\n" + exception.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getOtherErrorMessage(attemptedAction, exception.getErrorMessage())).build();
        }
    }

}
