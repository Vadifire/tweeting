package tweeting;

import dagger.Module;
import dagger.Provides;
import tweeting.services.TimelineCache;
import tweeting.services.TwitterService;

import javax.inject.Singleton;

@Module
public class TimelineCacheModule {

    @Provides
    @Singleton
    public TimelineCache provideTimelineCache() {
        return new TimelineCache(TwitterService.TIMELINE_SIZE);
    }
}
