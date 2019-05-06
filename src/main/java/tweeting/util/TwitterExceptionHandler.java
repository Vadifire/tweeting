package tweeting.util;

import twitter4j.TwitterException;

import javax.ws.rs.core.Response;


public class TwitterExceptionHandler {

    private String attemptedAction;

    public TwitterExceptionHandler(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }

    /*
     * Catches the TwitterException in resource classes and returns appropriate Response
     */
    public Response catchTwitterException(TwitterException e) {

        if (e.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                e.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            System.out.println("Twitter authentication failed. Please restart server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getAuthFailErrorMessage(attemptedAction)).build();

        } else if (e.isCausedByNetworkIssue()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getNetworkErrorMessage(attemptedAction)).build();
        } else { // 'Other' fail-safe
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(ResponseUtil.getOtherErrorMessage(attemptedAction, e.getErrorMessage())).build();
        }
    }

}
