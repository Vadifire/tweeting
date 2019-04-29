package main.health;

import com.codahale.metrics.health.HealthCheck;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;
import twitter4j.auth.NullAuthorization;

/*
 * This Health check reports 'unhealthy' if Twitter authentication is missing (null) and healthy otherwise.
 *
 * Run the following the view the health check status:
 * curl -i -X GET http://localhost:8081/healthcheck
 */

public class AuthHealthCheck extends HealthCheck {

    @Override
    protected Result check() {
        System.out.println("Checking authorization...");
        Twitter twitter = TwitterFactory.getSingleton();
        Authorization auth = twitter.getAuthorization();
        if (!auth.isEnabled()){
            return Result.unhealthy("Twitter authentication is not enabled. " +
                    "See http://twitter4j.org/en/configuration.html for help setting up authentication.");
        }
        return Result.healthy();
    }

}
