package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.util.ResponseUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.util.CharacterUtil;

import java.util.List;


public class TwitterService {

    private static TwitterService instance;

    private Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    public static final int MAX_TWEET_LENGTH = CharacterUtil.MAX_TWEET_LENGTH; // To not expose Twitter4J

    private TwitterService() {
    }

    public static TwitterService getInstance(TwitterOAuthCredentials auth) {
        try {
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e); // Log error message in cause initialization fails
            throw e;
        }
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

    public List<Status> getHomeTimeline() throws BadTwitterServiceResponseException {
        try {
            return api.getHomeTimeline();
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    public Status postTweet(String message) throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        // Prelim checks (avoid calling to Twitter if unnecessary)
        if (message == null) {
            throw new BadTwitterServiceCallException(ResponseUtil.getNullTweetErrorMessage());
        } else if (message.length() > MAX_TWEET_LENGTH || StringUtils.isBlank(message)) {
            throw new BadTwitterServiceCallException(ResponseUtil.getInvalidTweetErrorMessage());
        }
        try {
            return api.updateStatus(message);
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    private BadTwitterServiceResponseException createServerException(TwitterException te) {
        if (te.isCausedByNetworkIssue() || te.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                te.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            return new BadTwitterServiceResponseException(ResponseUtil.getServiceUnavailableErrorMessage(), te);
        } else {
            return new BadTwitterServiceResponseException(te);
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
