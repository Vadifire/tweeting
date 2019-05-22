package tweeting.services;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = TwitterServiceModule.class)
public interface TwitterServiceComponent {

    TwitterService buildService();
}
