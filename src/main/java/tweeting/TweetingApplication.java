package tweeting;

import tweeting.conf.AccessTokenDetails;
import tweeting.conf.AuthorizationDetails;
import tweeting.conf.ConsumerAPIDetails;
import tweeting.conf.TweetingConfiguration;
import tweeting.health.AliveHealthCheck;
import tweeting.resources.GetTimelineResource;
import tweeting.resources.PostTweetResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TweetingApplication extends Application<TweetingConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(TweetingApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Tweeting Service...");
        new TweetingApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
        logger.info("Initializing Tweeting Service...");
    }

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        logger.info("Running Tweeting Service on " + config.getHost() + ":" + config.getPort());


        /* Setup authorization with config values */
        AuthorizationDetails auth = config.getAuthorization();
        ConsumerAPIDetails consumerAPIDetails = auth.getConsumerAPIDetails();
        AccessTokenDetails accessTokenDetails = auth.getAccessTokenDetails();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(consumerAPIDetails.getApiKey());
        cb.setOAuthConsumerSecret(consumerAPIDetails.getApiSecretKey());
        cb.setOAuthAccessToken(accessTokenDetails.getAccessToken());
        cb.setOAuthAccessTokenSecret(accessTokenDetails.getAccessTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());

        logger.info("Authorization credentials set.");

        // Use Default API Impl (Twitter4J)
        Twitter api = tf.getInstance();

        // Verify authorization
        if (!api.getAuthorization().isEnabled()) {
            logger.error("Twitter authentication credentials are not set. Please restart server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help.");
            System.exit(1);
        }

        // Register alive health check
        env.healthChecks().register("AliveHealthCheck", new AliveHealthCheck());
        logger.info("Registered Health Check.");

        // Register GET timeline resource
        final GetTimelineResource timelineResource = new GetTimelineResource(api);
        env.jersey().register(timelineResource);
        logger.info("Registered Get Home Timeline Resource.");

        // Register POST tweet resource
        final PostTweetResource tweetResource = new PostTweetResource(api);
        env.jersey().register(tweetResource);
        logger.info("Registered Post Tweet Resource.");

    }
}