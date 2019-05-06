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

    @Test
    public void testGetConsumerKey() {
        String dummykey = "some key";
        config.setConsumerKey(dummykey);
        assertEquals(dummykey, config.getConsumerKey());
    }

    @Test
    public void testGetConsumerSecret() {
        String dummySecret = "some secret";
        config.setConsumerSecret(dummySecret);
        assertEquals(dummySecret, config.getConsumerSecret());
    }

    @Test
    public void testGetAccessToken() {
        String dummySecret = "some token";
        config.setAccessToken(dummySecret);
        assertEquals(dummySecret, config.getAccessToken());
    }

    @Test
    public void testGetAccessTokenSecret() {
        String dummySecret = "some token secret";
        config.setAccessTokenSecret(dummySecret);
        assertEquals(dummySecret, config.getAccessTokenSecret());
    }
}
