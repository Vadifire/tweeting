package tweeting.health;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class AliveHealthCheckTest {

    @Test
    public void testAliveHealthCheck() {
        AliveHealthCheck healthCheck = new AliveHealthCheck();
        assertEquals(HealthCheck.Result.healthy(), healthCheck.check()); // Ensure always returns healthy()
    }
}
