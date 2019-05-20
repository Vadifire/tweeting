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

    private TimelineCache timelineCache;

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    public static final int MAX_TWEET_LENGTH = CharacterUtil.MAX_TWEET_LENGTH; // To not expose Twitter4J

    public static final String SERVICE_UNAVAILABLE_MESSAGE = "Service is temporarily unavailable.";
    public static final String NULL_TWEET_MESSAGE = "Could not post tweet because message parameter is missing.";
    public static final String INVALID_TWEET_MESSAGE = "Could not post tweet because message was either blank or " +
            "longer than " + CharacterUtil.MAX_TWEET_LENGTH + " characters.";

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

        instance.timelineCache = new TimelineCache();

        return instance;
    }

    public static TwitterService getInstance() {
        if (instance == null) {
            instance = new TwitterService();
            instance.api = TwitterFactory.getSingleton();
            instance.timelineCache = new TimelineCache();
            logger.warn("TwitterService has been instantiated with no Twitter credentials. Please call getInstance" +
                    "with TwitterOAuthCredentials parameter to set credentials to support calls to Twitter.");
        }
        return instance;
    }

    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            logger.info("Successfully retrieved home timeline from Twitter.");
            if(timelineCache.isValid()) { // Avoid call to Twitter if valid cache
                return Optional.of(timelineCache.getTimeline());
            } else {
                Optional<List<Tweet>> timeline = constructTweetList(api.getHomeTimeline());
                if (timeline.isPresent()) { // Only cache non-null timelines
                    timelineCache.cache(timeline.get());
                }
                return timeline;
            }
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public Optional<Tweet> postTweet(String message) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (message != null ) {
            if (message.length() > MAX_TWEET_LENGTH || StringUtils.isBlank(message)) {
                throw new TwitterServiceCallException(INVALID_TWEET_MESSAGE);
            }
            try {
                logger.info("Successfully posted '{}' to Twitter.", message);
                Optional<Tweet> tweet = constructTweet(api.updateStatus(message));
                if (tweet.isPresent()) {
                    timelineCache.pushTweet(tweet.get());
                }
                return tweet;
            } catch (TwitterException te) {
                throw createServerException(te);
            }
        } else {
            throw new TwitterServiceCallException(NULL_TWEET_MESSAGE);
        }
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
        if (status == null) {
            return Optional.empty();
        }
        else {
            Tweet tweet = new Tweet();
            tweet.setMessage(status.getText());
            if (status.getUser() == null){
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
    }

    private Optional<List<Tweet>> constructTweetList(List<Status> statuses) {
        if (statuses == null) {
            return Optional.empty();
        } else {
            return Optional.of(statuses.stream()
                    .map(s -> constructTweet(s))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList()));
        }
    }

    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }

    public Twitter getAPI() {
        return api;
    }
}
