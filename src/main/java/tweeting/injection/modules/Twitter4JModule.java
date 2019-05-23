package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TweetingConfiguration;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Singleton;

/*
 * Provides a Twitter4J service for whatever client wants to use it
 *
 * Note: TweetingConfiguration is required for configuring the API the Twitter4JService relies on.
 */

@Module
public class Twitter4JModule {

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JModule.class);

    @Singleton
    @Provides
    Twitter provideTwitterAPI(TweetingConfiguration conf) {
        if (conf.getTwitterAuthorization() == null) {
            logger.error("TwitterService was built without valid " +
                    "Twitter Credentials. Twitter Credentials were null.", new NullPointerException());
            return TwitterFactory.getSingleton();
        }
        final TwitterOAuthCredentials auth = conf.getTwitterAuthorization();
        return new TwitterFactory(
                new ConfigurationBuilder()
                        .setDebugEnabled(true)
                        .setJSONStoreEnabled(true) // Need in order to use getRawJSON
                        .setOAuthConsumerKey(auth.getConsumerAPIKey())
                        .setOAuthConsumerSecret(auth.getConsumerAPISecretKey())
                        .setOAuthAccessToken(auth.getAccessToken())
                        .setOAuthAccessTokenSecret(auth.getAccessTokenSecret())
                        .build())
                .getInstance();
    }

    @Provides
    @Singleton
    TwitterService provideTwitterService(Twitter api) {
        return new Twitter4JService(api);
    }
}
