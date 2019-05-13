package tweeting.services;

import tweeting.conf.AccessTokenDetails;
import tweeting.conf.ConsumerAPIKeys;
import tweeting.conf.TwitterOAuthCredentials;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class TwitterService {

    private static TwitterService instance;

    private static Twitter api;

    private TwitterService() {
    }

    // 'Double Checked Locking' to make getInstance() thread safe
    public static TwitterService getInstance(TwitterOAuthCredentials auth) {
        if (instance == null) {
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
            instance = new TwitterService();
            instance.setAPI(twitterFactory.getInstance());
        }
        return instance;
    }

    public void setAPI(Twitter api) {
        this.api = api;
    }

    public List<Status> getTweets() throws TwitterException {
        return api.getHomeTimeline();
    }

    public Status postTweet(String message) throws TwitterException {
        return api.updateStatus(message);
    }

}
