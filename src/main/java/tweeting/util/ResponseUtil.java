package tweeting.util;

import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

/*
 * Utility Class that defines common HTTP Responses and TwitterException handling
 *
 * This reduces duplicate for resources
 */

public class ResponseUtil {

    private String attemptedAction;

    public ResponseUtil(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }

    public String getNetworkError() {
        return "Could not " + attemptedAction + " because connection to Twitter failed.";
    }

    public String getAuthFailError() {
        return "Could not " + attemptedAction + " because service is temporarily unavailable.";
    }

    public String getOtherError(String errorMessage) {
        return "Could not " + attemptedAction + ": " + errorMessage;
    }

    public String getNullResponseError() {
        return "Failed to " + attemptedAction + " from Twitter.";
    }

    public String getNullParamError(String missingParam) {
        return "Could not " + attemptedAction + " because no " + missingParam + " was specified.";
    }

    public String getParamBadLengthError(String param, String unit, int min, int max) {
        return "Could not " + attemptedAction + " because " + param + " must be between " + min + " and " + max +
                " " + unit + ".";
    }

    /*
     * Twitter Error Codes (https://developer.twitter.com/en/docs/basics/response-codes.html)
     */
    public enum TwitterErrorCode {

        BAD_AUTH_DATA(215), COULD_NOT_AUTH(32);

        TwitterErrorCode(int code) {
            this.code = code;
        }

        private int code;

        public int getCode() {
            return code;
        }
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
                    entity(getAuthFailError()).build();

        } else if (e.isCausedByNetworkIssue()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(getNetworkError()).build();
        } else { // 'Other' fail-safe
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(getOtherError(e.getErrorMessage())).build();
        }
    }

}
