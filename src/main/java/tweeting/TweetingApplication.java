package tweeting;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import tweeting.conf.AccessTokenDetails;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.conf.ConsumerAPIKeys;
import tweeting.conf.TweetingConfiguration;
import tweeting.health.AliveHealthCheck;
import tweeting.resources.GetTimelineResource;
import tweeting.resources.PostTweetResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import tweeting.util.LogFilter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class TweetingApplication extends Application<TweetingConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger("startupLogger");

    public static void main(String[] args) throws Exception {
        new TweetingApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
    }

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        logger.info("Starting Tweeting Service...");

        /* Setup authorization with config values */
        TwitterOAuthCredentials auth = config.getAuthorization();
        ConsumerAPIKeys consumerAPIKeys = auth.getConsumerAPIKeys();
        AccessTokenDetails accessTokenDetails = auth.getAccessTokenDetails();

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthConsumerKey(consumerAPIKeys.getConsumerAPIKey());
        configurationBuilder.setOAuthConsumerSecret(consumerAPIKeys.getConsumerAPISecretKey());
        configurationBuilder.setOAuthAccessToken(accessTokenDetails.getAccessToken());
        configurationBuilder.setOAuthAccessTokenSecret(accessTokenDetails.getAccessTokenSecret());
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());

        // Use Default API Impl (Twitter4J)
        Twitter api = twitterFactory.getInstance();

        // Verify authorization
        if (!api.getAuthorization().isEnabled()) {
            logger.error("Twitter authentication credentials are not set. Please restart server with " +
                    "valid credentials. See http://twitter4j.org/en/configuration.html for help."); // Should be fatal
            System.exit(1);
        }

        logger.info("Twitter authorization credentials set.");

        logger.debug("Twitter credentials are:\n" + // TODO: should credentials be logged?
                "\tConsumer API Key: " + consumerAPIKeys.getConsumerAPIKey() + "\n" +
                "\tConsumer API Secret Key: " + consumerAPIKeys.getConsumerAPIKey() + "\n" +
                "\tAccess Token: " + accessTokenDetails.getAccessToken() + "\n" +
                "\tAccess Token Secret: " + accessTokenDetails.getAccessTokenSecret());

        env.servlets().addFilter("Registered Logging Filter.", new LogFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        env.admin().addFilter("AdminFilter", new LogFilter()).addMappingForUrlPatterns(null,
                false, "/*");
        logger.info("Registered logging filter.");

        AliveHealthCheck healthCheck = new AliveHealthCheck();
        String healthCheckName = "Alive Health Check";
        env.healthChecks().register(healthCheckName, healthCheck);
        logger.info("Registered Health Check: " + healthCheckName);

        final GetTimelineResource timelineResource = new GetTimelineResource(api);
        env.jersey().register(timelineResource);
        logger.info("Registered Resource: " + timelineResource.getClass().getName() + ".");

        final PostTweetResource tweetResource = new PostTweetResource(api);
        env.jersey().register(tweetResource);
        logger.info("Registered Resource: " + tweetResource.getClass().getName() + ".");
    }
}