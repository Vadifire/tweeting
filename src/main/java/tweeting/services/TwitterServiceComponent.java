package tweeting.services;

import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TwitterOAuthCredentials;

import javax.inject.Singleton;

/*
 * This interface is used to generate the Injector.
 * The Injector is setup to inject Twitter API -> Service -> Resource.
 */
@Singleton
@Component(modules = {
        TwitterAPIModule.class,
        TwitterServiceModule.class
})
public interface TwitterServiceComponent {

    // Allows end-user (i.e. TweetingApplication) to configure/build Service by simply passing auth
    @Component.Builder
    interface Builder {
        @BindsInstance // https://dagger.dev/users-guide#binding-instances
        TwitterServiceComponent.Builder credentials(TwitterOAuthCredentials auth);
        TwitterServiceComponent build();
    }

    TwitterService buildService();
}