package tweeting;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TweetingConfigurationTest {

    // Class under test
    TweetingConfiguration config;

    @Before
    public void setup() {
        config = new TweetingConfiguration();
    }

    @Test
    public void testGetHost() {
        String dummyHost = "dummy host";
        config.setHost(dummyHost);
        assertEquals(dummyHost, config.getHost());
    }

    @Test
    public void testGetPort() {
        int dummyPort = 1234;
        config.setPort(dummyPort);
        assertEquals(dummyPort, config.getPort());
    }
}
