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
import java.util.stream.Collectors;


public class TwitterService {

    private static TwitterService instance;

    private Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    public static final int MAX_TWEET_LENGTH = CharacterUtil.MAX_TWEET_LENGTH; // To not expose Twitter4J

    public static String SERVICE_UNAVAILABLE_MESSAGE = "Service is temporarily unavailable.";
    public static String NULL_TWEET_MESSAGE = "Could not post tweet because message parameter is missing.";
    public static String INVALID_TWEET_MESSAGE = "Could not post tweet because message was either blank or longer than "
                  + CharacterUtil.MAX_TWEET_LENGTH + " characters.";

    private TwitterService() {
    }

    public static TwitterService getInstance(TwitterOAuthCredentials auth) {
        if (instance == null) {
            instance = new TwitterService();
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setJSONStoreEnabled(true); // Need in order to use getRawJSON
        configurationBuilder.setOAuthConsumerKey(auth.getConsumerAPIKey());
        configurationBuilder.setOAuthConsumerSecret(auth.getConsumerAPISecretKey());
        configurationBuilder.setOAuthAccessToken(auth.getAccessToken());
        configurationBuilder.setOAuthAccessTokenSecret(auth.getAccessTokenSecret());
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        instance.api = twitterFactory.getInstance();
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

    public List<Tweet> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            return constructTweetList(api.getHomeTimeline());
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public List<Tweet> getUserTimeline() throws TwitterServiceResponseException {
        try {
            return constructTweetList(api.getUserTimeline());
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public Tweet postTweet(String message) throws TwitterServiceResponseException, TwitterServiceCallException {
        // Prelim checks (avoid calling to Twitter if unnecessary)
        if (message == null) {
            throw new TwitterServiceCallException(NULL_TWEET_MESSAGE);
        } else if (message.length() > MAX_TWEET_LENGTH || StringUtils.isBlank(message)) {
            throw new TwitterServiceCallException(INVALID_TWEET_MESSAGE);
        }
        try {
            return constructTweet(api.updateStatus(message));
        } catch (TwitterException te) {
            throw createServerException(te);
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

    private Tweet constructTweet(Status status) {
        Tweet tweet = new Tweet();
        tweet.setMessage(status.getText());
        TwitterUser user = new TwitterUser();
        if (status.getUser() != null) {
            user.setTwitterHandle(status.getUser().getScreenName());
            user.setName(status.getUser().getName());
            user.setProfileImageUrl(status.getUser().getProfileImageURL());
            tweet.setUser(user);
        } else {
            logger.warn("Tweet has no user.");
        }
        tweet.setCreatedAt(status.getCreatedAt());
        return tweet;
    }

    private List<Tweet> constructTweetList(List<Status> statuses) {
        return statuses.stream()
                .map(s -> constructTweet(s))
                .collect(Collectors.toList());
    }

    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }

    public Twitter getAPI() {
        return api;
    }
}
