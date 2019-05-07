package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class AccessTokenDetailsTest {

    // Class under test
    AccessTokenDetails accessTokenDetails;

    @Before
    public void setup() {
        accessTokenDetails = new AccessTokenDetails();
    }

    @Test
    public void testGetAccessToken() {
        String dummyToken = "some token";
        accessTokenDetails.setAccessToken(dummyToken);
        assertEquals(dummyToken, accessTokenDetails.getAccessToken());
    }

    @Test
    public void testGetAccessTokenSecret() {
        String dummyKey = "some key";
        accessTokenDetails.setAccessTokenSecret(dummyKey);
        assertEquals(dummyKey, accessTokenDetails.getAccessTokenSecret());
    }
}
