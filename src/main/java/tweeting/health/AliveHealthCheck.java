package tweeting.health;

import com.codahale.metrics.health.HealthCheck;

/*
 * Used to report that the Server is still alive.
 *
 * Run the following the view the health check status:
 * curl -i -X GET http://localhost:8081/healthcheck
 */

public class AliveHealthCheck extends HealthCheck {

    @Override
    protected Result check() {
        return Result.healthy();
    }

}
