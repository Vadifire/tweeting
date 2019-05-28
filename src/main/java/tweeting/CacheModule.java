package tweeting;

import dagger.Module;
import dagger.Provides;
import tweeting.services.FilteredTimelineCache;
import tweeting.services.TimelineCache;

import javax.inject.Singleton;

@Module
public class CacheModule {

    @Provides
    @Singleton
    public TimelineCache provideTimelineCache() {
        return new TimelineCache();
    }

    @Provides
    @Singleton
    public FilteredTimelineCache provideFilteredCache() {
        return new FilteredTimelineCache();
    }
}
