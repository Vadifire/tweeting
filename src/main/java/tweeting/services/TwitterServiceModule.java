package tweeting.services;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class TwitterServiceModule {

    @Provides
    @Singleton
    static TwitterService provideTwitterService() {
        return Twitter4JService.getInstance();
    }
}
