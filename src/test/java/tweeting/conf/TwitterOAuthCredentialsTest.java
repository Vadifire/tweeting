package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class TwitterOAuthCredentialsTest {

    // Class under test
    TwitterOAuthCredentials twitterOAuthCredentials;

    @Before
    public void setup() {
        twitterOAuthCredentials = new TwitterOAuthCredentials();
    }

    @Test
    public void testGetAccessToken() {
        String dummyToken = "some token";
        twitterOAuthCredentials.setAccessToken(dummyToken);
        assertEquals(dummyToken, twitterOAuthCredentials.getAccessToken());
    }

    @Test
    public void testGetAccessTokenSecret() {
        String dummyKey = "some key";
        twitterOAuthCredentials.setAccessTokenSecret(dummyKey);
        assertEquals(dummyKey, twitterOAuthCredentials.getAccessTokenSecret());
    }

    @Test
    public void testGetApiKey() {
        String dummyKey = "some key";
        twitterOAuthCredentials.setConsumerAPIKey(dummyKey);
        assertEquals(dummyKey, twitterOAuthCredentials.getConsumerAPIKey());
    }

    @Test
    public void testGetApiSecretKey() {
        String dummySecret = "some secret";
        twitterOAuthCredentials.setConsumerAPISecretKey(dummySecret);
        assertEquals(dummySecret, twitterOAuthCredentials.getConsumerAPISecretKey());
    }
}
