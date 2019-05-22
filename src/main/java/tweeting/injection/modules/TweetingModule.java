package tweeting.injection.modules;

import com.codahale.metrics.health.HealthCheck;
import dagger.Module;
import dagger.Provides;
import tweeting.health.AliveHealthCheck;
import tweeting.resources.TwitterResource;
import tweeting.services.TwitterService;
import tweeting.util.LogFilter;

import javax.inject.Singleton;

/*
 * Provide services for TweetingApplication. Use Twitter4J for resources
 */

@Module(includes = Twitter4JModule.class)
public class TweetingModule {

    @Provides
    @Singleton
    static TwitterResource provideTwitterResource(TwitterService service) {
        return new TwitterResource(service);
    }

    @Provides
    @Singleton
    static HealthCheck provideHealthCheck() {
        return new AliveHealthCheck();
    }

    @Provides
    @Singleton
    static LogFilter provideLogger() {
        return new LogFilter();
    }
}
