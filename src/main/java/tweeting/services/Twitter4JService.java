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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Twitter4JService implements TwitterService {

    private static Twitter4JService instance;

    private Twitter api;
    private TwitterOAuthCredentials auth;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);


    private Twitter4JService() {
    }

    public static Twitter4JService getInstance() {
        if (instance == null) {
            instance = new Twitter4JService();
            instance.api = TwitterFactory.getSingleton(); // By default no credentials configured
        }
        return instance;
    }

    @Override
    public void setCredentials(TwitterOAuthCredentials auth) { // TODO: Might want to use dagger2 here
        api = new TwitterFactory(new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setJSONStoreEnabled(true) // Need in order to use getRawJSON
                .setOAuthConsumerKey(auth.getConsumerAPIKey())
                .setOAuthConsumerSecret(auth.getConsumerAPISecretKey())
                .setOAuthAccessToken(auth.getAccessToken())
                .setOAuthAccessTokenSecret(auth.getAccessTokenSecret())
                .build())
                .getInstance();
        this.auth = auth;
    }

    public TwitterOAuthCredentials getCredentials() { // TODO: make this less stupid
        return auth;
    }

    @Override
    public Optional<Tweet> postTweet(String message) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (StringUtils.isBlank(message)) {
            throw new TwitterServiceCallException(MISSING_TWEET_MESSAGE);
        }
        if (message.length() > MAX_TWEET_LENGTH) {
            throw new TwitterServiceCallException(TOO_LONG_TWEET_MESSAGE);
        }
        try {
            return Optional.ofNullable(api.updateStatus(message))
                    .map(status -> {
                        logger.info("Successfully posted '{}' to Twitter.", message);
                        return constructTweet(status);
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            return Optional.ofNullable(api.getHomeTimeline())
                    .map(statuses -> {
                        logger.info("Successfully retrieved home timeline.");
                        return constructTweetList(statuses);
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
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

    private Tweet constructTweet(Status status) {
        if (status == null) {
            return null;
        }
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
        return tweet;
    }

    private List<Tweet> constructTweetList(List<Status> statuses) {
        return statuses.stream()
                .map(this::constructTweet)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
/*
    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }

    public Twitter getAPI() {
        return api;
    }*/
}
