package tweeting.injection.modules;

import dagger.Module;
import dagger.Provides;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import twitter4j.Twitter;

import javax.inject.Singleton;

@Module
public class TwitterServiceModule {

    @Provides
    @Singleton
    static TwitterService provideTwitterService(Twitter api) {
        return new Twitter4JService(api);
    }
}
