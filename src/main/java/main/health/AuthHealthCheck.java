package main.health;

import com.codahale.metrics.health.HealthCheck;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;
import twitter4j.auth.NullAuthorization;

public class AuthHealthCheck extends HealthCheck {

    @Override
    protected Result check() {
        System.out.println("Checking authorization...");
        Twitter twitter = TwitterFactory.getSingleton();
        Authorization auth = twitter.getAuthorization();
        if (auth.getClass() == NullAuthorization.class){
            return Result.unhealthy("Twitter authentication failed. " +
                    "See http://twitter4j.org/en/configuration.html for help setting up authentication.");
        }
        return Result.healthy();
    }

}
