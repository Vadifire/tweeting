package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ConsumerAPIDetailsTest {

    // Class under test
    ConsumerAPIDetails consumerAPIDetails;

    @Before
    public void setup() {
        consumerAPIDetails = new ConsumerAPIDetails();
    }

    @Test
    public void testGetApiKey() {
        String dummyKey = "some key";
        consumerAPIDetails.setApiKey(dummyKey);
        assertEquals(dummyKey, consumerAPIDetails.getApiKey());
    }

    @Test
    public void testGetApiSecretKey() {
        String dummySecret = "some secret";
        consumerAPIDetails.setApiSecretKey(dummySecret);
        assertEquals(dummySecret, consumerAPIDetails.getApiSecretKey());
    }
}
