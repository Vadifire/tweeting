package tweeting.services;

import dagger.Module;
import dagger.Provides;
import twitter4j.Twitter;

import javax.inject.Singleton;

/*
 * This class is used provide the TwitterService dependency to any Clients (i.e. TwitterResource)
 */
@Module
public class TwitterServiceModule {

    @Provides
    @Singleton
    static TwitterService provideTwitterService(Twitter api) {
        return new Twitter4JService(api);
    }
}
