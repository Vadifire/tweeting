package tweeting.services;

import tweeting.models.Tweet;

import java.util.List;
import java.util.Optional;

/*
 * Defines API for Twitter Service without exposing implementation
 */
public interface TwitterService {

    int MAX_TWEET_LENGTH = 280; // Already available in twitter4j.util.CharacterUtil, but want to decouple

    String TWITTER_BASE_URL = "https://twitter.com/";
    String STATUS_DIRECTORY = "/status/";

    String SERVICE_UNAVAILABLE_MESSAGE = "Service is temporarily unavailable.";
    String MISSING_TWEET_MESSAGE = "Could not post tweet because message parameter is missing.";
    String TOO_LONG_TWEET_MESSAGE = "Could not post tweet because message is " +
            "longer than " + MAX_TWEET_LENGTH + " characters.";
    String MISSING_KEYWORD_MESSAGE = "Could not retrieve filtered timeline because keyword " +
            "parameter is missing.";
    String MISSING_PARENT_ID_MESSAGE = "Could not reply to tweet because no parent ID was specified.";

    Optional<Tweet> postTweet(String message) throws TwitterServiceResponseException, TwitterServiceCallException;

    Optional<Tweet> replyToTweet(Long parentId, String message)
            throws TwitterServiceResponseException, TwitterServiceCallException;

    Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException;

    Optional<List<Tweet>> getUserTimeline() throws TwitterServiceResponseException;

    Optional<List<Tweet>> getFilteredTimeline(String keyword)
            throws TwitterServiceResponseException, TwitterServiceCallException;
}
