package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.Twitter;

import javax.inject.Singleton;

/*
 * Provides a Twitter4J service for whatever client wants to use it
 *
 * Note: TweetingConfiguration is required for configuring the API the Twitter4JService relies on.
 */

@Module(includes = TwitterModule.class)
public class Twitter4JModule {

    @Provides
    @Singleton
    TwitterService provideTwitterService(Twitter api) {
        return new Twitter4JService(api);
    }
}
