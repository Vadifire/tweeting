package tweeting;

import dagger.Module;
import dagger.Provides;
import tweeting.services.TimelineCache;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.Twitter;

import javax.inject.Singleton;

/*
 * Provides a Twitter4J service for whatever client wants to use it
 */

@Module(includes = TwitterAPIModule.class) // Depends on Twitter API
public class Twitter4JModule {

    @Provides
    @Singleton
    TwitterService provideTwitterService(Twitter api) {
        return new Twitter4JService(api, new TimelineCache(TwitterService.TIMELINE_SIZE));
    }

}
