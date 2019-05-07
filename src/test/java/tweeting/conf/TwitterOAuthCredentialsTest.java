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
    public void testGetConsumerAPIKeys() {
        ConsumerAPIKeys mockedKeys = mock(ConsumerAPIKeys.class);
        twitterOAuthCredentials.setConsumerAPIKeys(mockedKeys);
        assertEquals(mockedKeys, twitterOAuthCredentials.getConsumerAPIKeys());
    }

    @Test
    public void testGetAccessTokenDetails() {
        AccessTokenDetails mockedTokenDetails = mock(AccessTokenDetails.class);
        twitterOAuthCredentials.setAccessTokenDetails(mockedTokenDetails);
        assertEquals(mockedTokenDetails, twitterOAuthCredentials.getAccessTokenDetails());
    }
}
