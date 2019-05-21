package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.models.Tweet;
import tweeting.models.TwitterUser;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.util.CharacterUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitterService {

    private static TwitterService instance;

    private Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    public static final int MAX_TWEET_LENGTH = CharacterUtil.MAX_TWEET_LENGTH; // To not expose Twitter4J

    public static final String SERVICE_UNAVAILABLE_MESSAGE = "Service is temporarily unavailable.";
    public static final String MISSING_TWEET_MESSAGE = "Could not post tweet because message parameter is missing.";
    public static final String TOO_LONG_TWEET_MESSAGE = "Could not post tweet because message is " +
            "longer than " + CharacterUtil.MAX_TWEET_LENGTH + " characters.";
    public static final String MISSING_KEYWORD_MESSAGE = "Could not retrieve filtered timeline because keyword " +
            "parameter is missing.";

    private TwitterService() {
    }

    public static TwitterService getInstance(TwitterOAuthCredentials auth) {
        if (instance == null) {
            instance = new TwitterService();
        }

        instance.api = new TwitterFactory(new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setJSONStoreEnabled(true) // Need in order to use getRawJSON
                .setOAuthConsumerKey(auth.getConsumerAPIKey())
                .setOAuthConsumerSecret(auth.getConsumerAPISecretKey())
                .setOAuthAccessToken(auth.getAccessToken())
                .setOAuthAccessTokenSecret(auth.getAccessTokenSecret())
                .build())
                .getInstance();

        return instance;
    }

    public static TwitterService getInstance() {
        if (instance == null) {
            instance = new TwitterService();
            TwitterFactory.getSingleton();
            logger.warn("TwitterService has been instantiated with no Twitter credentials. Please call getInstance" +
                    "with TwitterOAuthCredentials parameter to set credentials to support calls to Twitter.");
        }
        return instance;
    }

    public Optional<Tweet> postTweet(String message) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (StringUtils.isBlank(message)) {
            throw new TwitterServiceCallException(MISSING_TWEET_MESSAGE);
        }
        if (message.length() > MAX_TWEET_LENGTH) {
            throw new TwitterServiceCallException(TOO_LONG_TWEET_MESSAGE);
        }
        try {
            final Optional<Tweet> postedTweet = Optional.ofNullable(api.updateStatus(message))
                    .map(this::constructTweet)
                    .orElse(Optional.empty());
            postedTweet.ifPresent(t -> logger.info("Successfully posted '{}' to Twitter.", message));
            return postedTweet;
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            final Optional<List<Tweet>> tweets = Optional.ofNullable(api.getHomeTimeline())
                    .map(this::constructTweetList)
                    .orElse(Optional.empty());
            tweets.ifPresent(t -> logger.info("Successfully retrieved home timeline."));
            return tweets;
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public Optional<List<Tweet>> getFilteredTimeline(String keyword) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (StringUtils.isBlank(keyword)) {
            throw new TwitterServiceCallException(MISSING_KEYWORD_MESSAGE);
        }
        return getHomeTimeline().map(tweets -> tweets.stream()
                .filter(t -> StringUtils.containsIgnoreCase(t.getMessage(), keyword))
                .collect(Collectors.toList())
        );
    }

    private TwitterServiceResponseException createServerException(TwitterException te) {
        if (te.isCausedByNetworkIssue() || te.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                te.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            return new TwitterServiceResponseException(SERVICE_UNAVAILABLE_MESSAGE, te);
        } else {
            return new TwitterServiceResponseException(te);
        }
    }

    private Optional<Tweet> constructTweet(Status status) {
        Tweet tweet = new Tweet();
        tweet.setMessage(status.getText());
        if (status.getUser() == null) {
            logger.warn("Tweet has no user.");
        } else {
            TwitterUser user = new TwitterUser();
            user.setTwitterHandle(status.getUser().getScreenName());
            user.setName(status.getUser().getName());
            user.setProfileImageUrl(status.getUser().getProfileImageURL());
            tweet.setUser(user);
        }
        tweet.setCreatedAt(status.getCreatedAt());
        return Optional.of(tweet);
    }

    private Optional<List<Tweet>> constructTweetList(List<Status> statuses) {
        return Optional.of(statuses.stream()
                .map(this::constructTweet)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }

    public Twitter getAPI() {
        return api;
    }
}
