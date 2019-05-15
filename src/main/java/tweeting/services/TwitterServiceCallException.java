package tweeting.services;

/*
 * An exception that occurs while using TwitterService, but fails before actually making a call to Twitter.
 */
public class TwitterServiceCallException extends Exception {

    public TwitterServiceCallException(String message) {
        super(message);
    }
}
