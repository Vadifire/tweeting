package tweeting.services;

import dagger.BindsInstance;
import dagger.Component;
import tweeting.conf.TwitterOAuthCredentials;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
        TwitterAPIModule.class,
        TwitterServiceModule.class
})
public interface TwitterServiceComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        TwitterServiceComponent.Builder credentials(@Named TwitterOAuthCredentials auth); // TODO: look into Named annotation
        TwitterServiceComponent build();
    }

    TwitterService buildService();
}