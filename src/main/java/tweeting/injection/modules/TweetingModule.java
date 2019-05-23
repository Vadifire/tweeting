package tweeting.injection.modules;

import com.codahale.metrics.health.HealthCheck;
import dagger.Module;
import dagger.Provides;
import tweeting.health.AliveHealthCheck;
import tweeting.util.LogFilter;

import javax.inject.Singleton;

/*
 * Provide services for TweetingApplication.
 */

@Module(includes = Twitter4JModule.class)
public class TweetingModule {

    @Provides
    @Singleton
    HealthCheck provideAliveHealthCheck() {
        return new AliveHealthCheck();
    }

    @Provides
    @Singleton
    LogFilter provideLogFilter() {
        return new LogFilter();
    }
}
