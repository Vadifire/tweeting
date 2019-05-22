package tweeting.services;

import dagger.Module;
import dagger.Provides;
import tweeting.conf.TwitterOAuthCredentials;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
class TwitterAPIModule {
    @Provides
    @Singleton
    static Twitter provideTwitterAPI(@Named TwitterOAuthCredentials auth) {
        return new TwitterFactory(new ConfigurationBuilder()
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
