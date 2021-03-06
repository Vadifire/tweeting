package tweeting.services;

import twitter4j.TwitterException;

/*
 * An exception that occurs while using TwitterService and fails while making a call to Twitter
 */
public class TwitterServiceResponseException extends Exception {

    public TwitterServiceResponseException(TwitterException cause) {
        super(cause.getErrorMessage(), cause);
    }

    public TwitterServiceResponseException(String message, TwitterException cause) {
        super(message, cause);
    }

}
