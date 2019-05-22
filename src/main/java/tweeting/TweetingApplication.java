package tweeting;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TweetingConfiguration;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.health.AliveHealthCheck;
import tweeting.resources.TwitterResource;
import tweeting.services.DaggerTwitterServiceComponent;
import tweeting.services.TwitterService;
import tweeting.util.LogFilter;

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
        try {
            logger.debug("Configuring Tweeting application");
            TwitterOAuthCredentials auth = config.getAuthorization();
            TwitterService service = DaggerTwitterServiceComponent.builder()
                    .credentials(auth)
                    .build()
                    .buildService();

            logger.info("Twitter credentials have been configured using the {} configuration file.",
                    getConfigFileName());

            // Use Default API Impl (Twitter4J)

            logger.debug("Adding log filter");
            LogFilter logFilter = new LogFilter();
            env.servlets().addFilter("Log Filter", logFilter)
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
            env.admin().addFilter("AdminFilter", new LogFilter()).addMappingForUrlPatterns(
                    null, false, "/*");
            logger.debug("Log Filter has been set to: {}", logFilter.getClass().getName());

            logger.debug("Registering health check");
            AliveHealthCheck healthCheck = new AliveHealthCheck();
            String healthCheckName = "Alive Health Check";
            env.healthChecks().register(healthCheckName, healthCheck);
            logger.debug("Health check has been registered: {}", healthCheck.getClass().getName());

            logger.debug("Registering Resource");
            final TwitterResource twitterResource = new TwitterResource(service);
            env.jersey().register(twitterResource);
            logger.debug("Registered resource: {}", twitterResource.getClass().getName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static String getConfigFileName() {
        return configFileName;
    }
}