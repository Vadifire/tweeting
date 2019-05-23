package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import tweeting.conf.TweetingConfiguration;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.services.TimelineCache;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Singleton;

/*
 * Provides a Twitter4J service for whatever client wants to use it
 *
 * Note: TwitterOAuthCredentials are required for configuring the API the service relies on.
 */

@Module
public class Twitter4JModule {

    @Provides
    @Singleton
    static TwitterService provideTwitterService(TweetingConfiguration conf) {
        final TwitterOAuthCredentials auth = conf.getTwitterAuthorization();
        final TimelineCache cache = new TimelineCache(TwitterService.TIMELINE_SIZE); // TODO: consider interface
        return new Twitter4JService(
                new TwitterFactory(
                        new ConfigurationBuilder()
                                .setDebugEnabled(true)
                                .setJSONStoreEnabled(true) // Need in order to use getRawJSON
                                .setOAuthConsumerKey(auth.getConsumerAPIKey())
                                .setOAuthConsumerSecret(auth.getConsumerAPISecretKey())
                                .setOAuthAccessToken(auth.getAccessToken())
                                .setOAuthAccessTokenSecret(auth.getAccessTokenSecret())
                                .build())
                .getInstance(), cache);
    }
}
