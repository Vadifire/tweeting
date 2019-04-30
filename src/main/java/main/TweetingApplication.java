package main;

import main.health.AliveHealthCheck;
import main.resources.GetTimelineResource;
import main.resources.PostTweetResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TweetingApplication extends Application<TweetingConfiguration> {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Tweeting Service...");
        new TweetingApplication().run(args);
    }

    @Override
    public String getName() {
        return "tweeting";
    }

    @Override
    public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
        System.out.println("Initializing Tweeting Service...");

        Twitter twitter = TwitterFactory.getSingleton();
        if (!twitter.getAuthorization().isEnabled()) {
            System.out.println("Twitter authentication credentials are not set. Please restart Server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help.");
            System.exit(1);
        }
    }

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        System.out.println("Running Tweeting Service...");

        //Register alive health check
        env.healthChecks().register("AliveHealthCheck", new AliveHealthCheck());

        // Register GET timeline resource
        final GetTimelineResource timelineResource = new GetTimelineResource();
        env.jersey().register(timelineResource);

        //Register POST tweet resource
        final PostTweetResource tweetResource = new PostTweetResource();
        env.jersey().register(tweetResource);
    }
}