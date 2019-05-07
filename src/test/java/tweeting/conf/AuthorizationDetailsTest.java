package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class AuthorizationDetailsTest {

    // Class under test
    AuthorizationDetails authorizationDetails;

    @Before
    public void setup() {
        authorizationDetails = new AuthorizationDetails();
    }

    @Test
    public void testGetConsumerAPIDetails() {
        ConsumerAPIDetails mockedConsumerDetails = mock(ConsumerAPIDetails.class);
        authorizationDetails.setConsumerAPIDetails(mockedConsumerDetails);
        assertEquals(mockedConsumerDetails, authorizationDetails.getConsumerAPIDetails());
    }

    @Test
    public void testGetAccessTokenDetails() {
        AccessTokenDetails mockedConsumerDetails = mock(AccessTokenDetails.class);
        authorizationDetails.setAccessTokenDetails(mockedConsumerDetails);
        assertEquals(mockedConsumerDetails, authorizationDetails.getAccessTokenDetails());
    }
}
