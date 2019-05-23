package tweeting.injection.components;

import com.codahale.metrics.health.HealthCheck;
import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TweetingConfiguration;
import tweeting.injection.modules.TweetingModule;
import tweeting.resources.TwitterResource;
import tweeting.util.LogFilter;

import javax.inject.Singleton;

/*
 * This interface is used to generate the Injector.
 * The Injector is setup to inject the necessary services to the TweetingApplication.
 */
@Singleton
@Component(modules = {
        TweetingModule.class
})
public interface TweetingComponent {

    // Allows end-user (i.e. TweetingApplication) to configure/build Service by simply passing auth
    @Component.Builder
    interface Builder {
        @BindsInstance // https://dagger.dev/users-guide#binding-instances
        TweetingComponent.Builder configuration(TweetingConfiguration conf);
        TweetingComponent build();
    }

    TwitterResource buildTwitterResource();
    LogFilter buildLogFilter();
    HealthCheck buildHealthCheck();
}