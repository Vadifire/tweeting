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

    public List<Status> getTweets() throws TwitterServiceException {
        try {
            return api.getHomeTimeline();
        } catch (TwitterException te) {
            throw createTwitterServiceException(te);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Status postTweet(String message) throws TwitterServiceException {
        // Prelim checks (avoid calling to Twitter if unnecessary)
        if (message == null) {
            TwitterServiceException e = new TwitterServiceException("Request is missing message parameter.",
                    new NullPointerException("message"));
            throw e;
        }
        if (StringUtils.isBlank(message)) {
            TwitterServiceException e = new TwitterServiceException("Message parameter is blank.",
                    TwitterErrorCode.MESSAGE_BLANK);
            throw e;
        } else if (message.length() > MAX_TWEET_LENGTH) {
            TwitterServiceException e = new TwitterServiceException("Message parameter is over the  "
                    + MAX_TWEET_LENGTH + " character limit.", TwitterErrorCode.MESSAGE_TOO_LONG);
            throw e;
        }

        try {
            return api.updateStatus(message);
        } catch (TwitterException te) {
            throw createTwitterServiceException(te);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private TwitterServiceException createTwitterServiceException(TwitterException te) {
        if (te.isCausedByNetworkIssue()) {
            return new TwitterServiceException("No response from Twitter.", te);
        } else {
            return new TwitterServiceException(te); //Default builder
        }
    }

    // Used for mocking purposes
    public void setAPI(Twitter api) {
        this.api = api;
    }
}
