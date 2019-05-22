package tweeting.resources;

import dagger.Module;
import dagger.Provides;
import tweeting.services.TwitterService;

import javax.inject.Singleton;

@Module
public class TwitterResourceModule {
    @Provides
    @Singleton
    static TwitterResource provideTwitterResource(TwitterService service) {
        return new TwitterResource(service);
    }
}
