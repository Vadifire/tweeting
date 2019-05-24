package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TweetingConfiguration;
import tweeting.conf.TwitterOAuthCredentials;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Singleton;

/*
 * Provides Twitter API
 * Note: TweetingConfiguration is required as a dependency.
 */

@Module
public class TwitterAPIModule {

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JModule.class);

    @Provides
    @Singleton
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

}
