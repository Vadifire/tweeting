package tweeting;

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
import tweeting.util.RequestsLogFilter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class TweetingApplication extends Application<TweetingConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(TweetingApplication.class);
    private static String configFileName;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            logger.error("Invalid arguments. First argument should be 'server' and second argument should point to " +
                    "config file.");
        }
        configFileName = args[1]; // Store to log which configuration file was used
        new TweetingApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
    }

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        logger.trace("Configuring Tweeting Application...");

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
        logger.info("Twitter credentials have been configured using {} configuration file.", getConfigFileName());

        // Use Default API Impl (Twitter4J)
        Twitter api = twitterFactory.getInstance();

        RequestsLogFilter logFilter = new RequestsLogFilter();
        env.servlets().addFilter("Requests Log Filter", logFilter)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        env.admin().addFilter("AdminFilter", new RequestsLogFilter()).addMappingForUrlPatterns(null,
                false, "/*");

        logger.debug("Logging Filter for HTTP requests has been set to: {}", logFilter.getClass().getName());

        AliveHealthCheck healthCheck = new AliveHealthCheck();
        String healthCheckName = "Alive Health Check";
        env.healthChecks().register(healthCheckName, healthCheck);

        // Resource URIs and server IP/port already logged by dropwizard.
        final GetTimelineResource timelineResource = new GetTimelineResource(api);
        env.jersey().register(timelineResource);
        final PostTweetResource tweetResource = new PostTweetResource(api);
        env.jersey().register(tweetResource);
    }

    public static String getConfigFileName() {
        return configFileName;
    }
}