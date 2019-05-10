package tweeting.health;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Used to report that the Server is still alive.
 *
 * Run the following the view the health check status:
 * curl -i -X GET http://localhost:8081/healthcheck
 */

public class AliveHealthCheck extends HealthCheck {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    @Override
    public Result check() {
        try {
            logger.info("Application is healthy. Sending 200 OK response.");
            return Result.healthy();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.unhealthy("Server encountered error while performing health check.");
        }
    }

}
