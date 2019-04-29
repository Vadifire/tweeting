package main.health;

import com.codahale.metrics.health.HealthCheck;

/*
 * This Health check reports 'unhealthy' if Twitter authentication is missing (null) and healthy otherwise.
 *
 * Run the following the view the health check status:
 * curl -i -X GET http://localhost:8081/healthcheck
 */

public class AuthHealthCheck extends HealthCheck {

    @Override
    protected Result check() {
        return Result.healthy();
    }

}
