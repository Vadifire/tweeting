package tweeting.services;

/*
 * An exception that occurs while using TwitterService, but fails before actually making a call to Twitter.
 */
public class BadTwitterServiceCallException extends Exception {

    public BadTwitterServiceCallException(String message) {
        super(message);
    }
}
