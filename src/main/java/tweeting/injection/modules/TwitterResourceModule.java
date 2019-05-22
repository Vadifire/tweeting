package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import tweeting.resources.TwitterResource;
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
