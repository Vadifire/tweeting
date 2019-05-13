package tweeting.util;

import twitter4j.TwitterException;

public class TwitterServiceException extends Exception {

    private TwitterException twitterException;

    public TwitterServiceException(TwitterException te) {
        this.twitterException = te;
    }

    public int getErrorCode() {
        return twitterException.getErrorCode();
    }

    public String getErrorMessage() {
        return twitterException.getErrorMessage();
    }

    public boolean isCausedByNetworkIssue() {
        return twitterException.isCausedByNetworkIssue();
    }
}
