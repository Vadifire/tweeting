package tweeting.injection.components;

import com.codahale.metrics.health.HealthCheck;
import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.injection.modules.TweetingModule;
import tweeting.resources.TwitterResource;
import tweeting.util.LogFilter;

import javax.inject.Singleton;

/*
 * This interface is used to generate the Injector.
 * The Injector is setup to inject Twitter API -> Service -> Resource.
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
        TweetingComponent.Builder credentials(TwitterOAuthCredentials auth);
        TweetingComponent build();
    }

    TwitterResource buildResource();
    LogFilter buildLogFilter();
    HealthCheck buildHealthCheck();
}