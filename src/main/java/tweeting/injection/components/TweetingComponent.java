package tweeting.injection.components;

import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TweetingConfiguration;
import tweeting.health.AliveHealthCheck;
import tweeting.injection.modules.Twitter4JModule;
import tweeting.resources.TwitterResource;
import tweeting.util.LogFilter;

import javax.inject.Singleton;

/*
 * This interface is used to generate the Injector.
 * The Injector is setup to inject the necessary services to the TweetingApplication.
 */
@Singleton
@Component(modules = {
        Twitter4JModule.class
})
public interface TweetingComponent {

    // Allows end-user (i.e. TweetingApplication) to configure/build services by simply passing TweetingConfiguration
    @Component.Builder
    interface Builder {
        @BindsInstance // https://dagger.dev/users-guide#binding-instances
        TweetingComponent.Builder configuration(TweetingConfiguration conf);
        TweetingComponent build();
    }

    TwitterResource buildTwitterResource();
    LogFilter buildLogFilter();
    AliveHealthCheck buildAliveHealthCheck();
}