package tweeting.util;

/*
 * Utility Class that defines common HTTP Response messages
 *
 * This reduces duplicate code for resources
 */

import twitter4j.util.CharacterUtil;

public class ResponseUtil {

    public static String getServiceUnavailableErrorMessage() {
        return "Service is temporarily unavailable.";
    }

    public static String getNullTweetErrorMessage() {
        return "Could not post tweet because message parameter is missing.";
    }

    public static String getInvalidTweetErrorMessage() {
        return "Could not post tweet because message was either blank or longer than " + CharacterUtil.MAX_TWEET_LENGTH
                + " characters.";
    }

}
