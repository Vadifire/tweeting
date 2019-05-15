package tweeting.services;

import twitter4j.TwitterException;

/*
 * An exception that occurs while using TwitterService and fails while making a call to Twitter
 */
public class BadTwitterServiceResponseException extends Exception {

    public BadTwitterServiceResponseException(TwitterException cause) {
        super(cause.getErrorMessage(), cause);
    }

    public BadTwitterServiceResponseException(String message, TwitterException cause) {
        super(message, cause);
    }

}
