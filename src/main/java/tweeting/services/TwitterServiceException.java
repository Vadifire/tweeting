package tweeting.services;

import twitter4j.TwitterException;

public class TwitterServiceException extends Exception {

    private TwitterException twitterException;
    private int errorCode;

    public TwitterServiceException(TwitterException te) {
        super(te.isCausedByNetworkIssue() ? "No response from Twitter" : te.getErrorMessage(), te);
        this.twitterException = te;
    }

    // Create Exception with custom error code
    public TwitterServiceException(String message, TwitterErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    // Used for parameter NullPointerExceptions
    public TwitterServiceException(String message, Exception cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        if (twitterException == null) { // Can define own error code if cause is not TwitterException
            return errorCode;
        }
        return twitterException.getErrorCode();
    }

    public boolean isCausedByNetworkIssue() {
        if (twitterException == null) {
            return false;
        }
        return twitterException.isCausedByNetworkIssue();
    }

    public boolean isCausedByNullParam() {
        return (this.getCause() instanceof NullPointerException);
    }

    public String getMissingParam() {
        if (this.getCause() instanceof NullPointerException) {
            return this.getCause().getMessage(); //
        } else {
            return null;
        }
    }
}
