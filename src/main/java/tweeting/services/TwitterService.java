package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.AccessTokenDetails;
import tweeting.conf.ConsumerAPIKeys;
import tweeting.conf.TwitterOAuthCredentials;
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
            ConsumerAPIKeys consumerAPIKeys = auth.getConsumerAPIKeys();
            AccessTokenDetails accessTokenDetails = auth.getAccessTokenDetails();
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(true);
            configurationBuilder.setJSONStoreEnabled(true); // Need in order to use getRawJSON
            configurationBuilder.setOAuthConsumerKey(consumerAPIKeys.getConsumerAPIKey());
            configurationBuilder.setOAuthConsumerSecret(consumerAPIKeys.getConsumerAPISecretKey());
            configurationBuilder.setOAuthAccessToken(accessTokenDetails.getAccessToken());
            configurationBuilder.setOAuthAccessTokenSecret(accessTokenDetails.getAccessTokenSecret());
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

    public List<Status> getTweets() throws BadTwitterServiceResponseException {
        try {
            return api.getHomeTimeline();
        } catch (TwitterException te) {
            throw createServerException(te);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Status postTweet(String message) throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        // Prelim checks (avoid calling to Twitter if unnecessary)
        if (message == null) {
            throw new BadTwitterServiceCallException("Message parameter is missing.");
        }
        if (StringUtils.isBlank(message)) {
            throw new BadTwitterServiceCallException("Message parameter is blank.");
        } else if (message.length() > MAX_TWEET_LENGTH) {
            throw new BadTwitterServiceCallException("Message parameter is over the " + MAX_TWEET_LENGTH +
                    " character limit.");
        }
        try {
            return api.updateStatus(message);
        } catch (TwitterException te) {
            throw createServerException(te);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private BadTwitterServiceResponseException createServerException(TwitterException te) {
        if (te.isCausedByNetworkIssue() || te.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                te.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            return new BadTwitterServiceResponseException("Service is temporarily unavailable.", te);
        } else {
            return new BadTwitterServiceResponseException(te);
        }
    }

    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }
}
