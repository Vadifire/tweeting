package tweeting;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.conf.TweetingConfiguration;
import tweeting.resources.TwitterResource;
import tweeting.util.LogFilter;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class TweetingApplication extends Application<TweetingConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(TweetingApplication.class);
    private static String configFileName;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            logger.error("Invalid arguments. First argument should be 'server' and second argument should point to " +
                    "configuration file.");
        }
        configFileName = args[1]; // Store to log which configuration file was used
        new TweetingApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
    }

    @Inject
    HealthCheck healthCheck;

    @Inject
    LogFilter logFilter;

    @Override
    public void run(TweetingConfiguration config, Environment env) {
        try {
            logger.debug("Configuring Tweeting application");

            /* INJECTION */
            final TweetingComponent comp = DaggerTweetingComponent.builder()
                    .configuration(config)
                    .build();
            TwitterResource twitterResource = comp.buildTwitterResource();
            healthCheck = comp.buildAliveHealthCheck();
            logFilter = comp.buildLogFilter();

            logger.info("Twitter credentials have been configured using the {} configuration file.",
                    getConfigFileName());

            env.servlets().addFilter("Log Filter", logFilter)
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
            env.admin().addFilter("AdminFilter", new LogFilter()).addMappingForUrlPatterns(
                    null, false, "/*");
            logger.info("Log Filter has been set to: {}", logFilter.getClass().getName());

            final FilterRegistration.Dynamic cors =
                    env.servlets().addFilter("CORS", CrossOriginFilter.class);
            cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
            cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST");
            cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
            logger.info("Configured CORS policy: " + cors.getInitParameters());

            env.healthChecks().register("Alive Health Check", healthCheck);
            logger.info("Health check has been registered: {}", healthCheck.getClass().getName());

            env.jersey().register(twitterResource);
            logger.info("Registered resource: {}", twitterResource.getClass().getName());


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static String getConfigFileName() {
        return configFileName;
    }
}