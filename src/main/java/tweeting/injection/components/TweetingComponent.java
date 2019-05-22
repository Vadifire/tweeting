package tweeting.injection.components;

import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.resources.TwitterResource;
import tweeting.injection.modules.TwitterResourceModule;
import tweeting.injection.modules.TwitterAPIModule;
import tweeting.injection.modules.TwitterServiceModule;

import javax.inject.Singleton;

/*
 * This interface is used to generate the Injector.
 * The Injector is setup to inject Twitter API -> Service -> Resource.
 */
@Singleton
@Component(modules = {
        TwitterAPIModule.class,
        TwitterServiceModule.class,
        TwitterResourceModule.class
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
}