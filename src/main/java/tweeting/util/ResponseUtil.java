package tweeting.util;

/*
 * Utility Class that defines common HTTP Response messages
 *
 * This reduces duplicate code for resources
 */

public class ResponseUtil {

    public static String getNetworkErrorMessage(String attemptedAction) {
        return "Could not " + attemptedAction + " because connection to Twitter failed.";
    }

    public static String getServiceUnavailableErrorMessage(String attemptedAction) {
        return "Could not " + attemptedAction + " because service is temporarily unavailable.";
    }

    public static String getOtherErrorMessage(String attemptedAction, String errorMessage) {
        return "Could not " + attemptedAction + ": " + errorMessage;
    }

    public static String getNullResponseErrorMessage(String attemptedAction) {
        return "Failed to " + attemptedAction + " from Twitter.";
    }

    public static String getNullParamErrorMessage(String attemptedAction, String missingParam) {
        return "Could not " + attemptedAction + " because no " + missingParam + " was specified.";
    }

    public static String getParamBadLengthErrorMessage(String attemptedAction, String param, String unit, int max) {
        return "Could not " + attemptedAction + " because " + param + " must be not blank and within " + max + " " +
                unit;
    }

}
