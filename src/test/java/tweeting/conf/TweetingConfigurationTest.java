package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class TweetingConfigurationTest {

    // Class under test
    TweetingConfiguration config;

    @Before
    public void setup() {
        config = new TweetingConfiguration();
    }

    @Test
    public void testGetAuthorization() {
        AuthorizationDetails mockedAuth = mock(AuthorizationDetails.class);
        config.setAuthorization(mockedAuth);
        assertEquals(mockedAuth, config.getAuthorization());
    }

}
