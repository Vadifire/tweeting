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
    public void testGetConsumerAPIDetails() {
        ConsumerAPIKeys mockedConsumerDetails = mock(ConsumerAPIKeys.class);
        twitterOAuthCredentials.setConsumerAPIDetails(mockedConsumerDetails);
        assertEquals(mockedConsumerDetails, twitterOAuthCredentials.getConsumerAPIDetails());
    }

    @Test
    public void testGetAccessTokenDetails() {
        AccessTokenDetails mockedConsumerDetails = mock(AccessTokenDetails.class);
        twitterOAuthCredentials.setAccessTokenDetails(mockedConsumerDetails);
        assertEquals(mockedConsumerDetails, twitterOAuthCredentials.getAccessTokenDetails());
    }
}
