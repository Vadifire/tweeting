package tweeting;

import dagger.Module;
import dagger.Provides;
import tweeting.services.FilteredTimelineCache;
import tweeting.services.TimelineCache;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.Twitter;

import javax.inject.Singleton;

/*
 * Provides a Twitter4J service for whatever client wants to use it
 */

@Module(includes = {
        TwitterAPIModule.class,
        CacheModule.class
})
public class Twitter4JModule {

    @Provides
    @Singleton
    TwitterService provideTwitterService(Twitter api, TimelineCache timelineCache,
                                         FilteredTimelineCache filteredTimelineCache) {
        return new Twitter4JService(api, timelineCache, filteredTimelineCache);
    }
}
