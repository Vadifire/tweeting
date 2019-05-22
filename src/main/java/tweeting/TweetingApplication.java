package tweeting;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TweetingConfiguration;
import tweeting.injection.components.DaggerTweetingComponent;
import tweeting.injection.components.TweetingComponent;
import tweeting.resources.TwitterResource;
import tweeting.util.LogFilter;

import javax.inject.Inject;
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

    @Inject
    LogFilter logFilter;

    @Inject
    HealthCheck healthCheck;

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        try {
            logger.debug("Configuring Tweeting application");

            /* INJECTION */
            final TweetingComponent comp = DaggerTweetingComponent.builder()
                    .credentials(config.getAuthorization())
                    .build();
            TwitterResource twitterResource = comp.buildTwitterResource();
            logFilter = comp.buildLogFilter();
            healthCheck = comp.buildHealthCheck();

            logger.info("Twitter credentials have been configured using the {} configuration file.",
                    getConfigFileName());

            logger.debug("Adding log filter");
            env.servlets().addFilter("Log Filter", logFilter)
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
            env.admin().addFilter("AdminFilter", new LogFilter()).addMappingForUrlPatterns(
                    null, false, "/*");
            logger.debug("Log Filter has been set to: {}", logFilter.getClass().getName());

            logger.debug("Registering health check");
            String healthCheckName = "Alive Health Check";
            env.healthChecks().register(healthCheckName, healthCheck);
            logger.debug("Health check has been registered: {}", healthCheck.getClass().getName());

            logger.debug("Registering Resource");
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