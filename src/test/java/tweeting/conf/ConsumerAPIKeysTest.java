package tweeting.conf;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ConsumerAPIKeysTest {

    // Class under test
    ConsumerAPIKeys consumerAPIKeys;

    @Before
    public void setup() {
        consumerAPIKeys = new ConsumerAPIKeys();
    }

    @Test
    public void testGetApiKey() {
        String dummyKey = "some key";
        consumerAPIKeys.setConsumerAPIKey(dummyKey);
        assertEquals(dummyKey, consumerAPIKeys.getConsumerAPIKey());
    }

    @Test
    public void testGetApiSecretKey() {
        String dummySecret = "some secret";
        consumerAPIKeys.setConsumerAPISecretKey(dummySecret);
        assertEquals(dummySecret, consumerAPIKeys.getConsumerAPISecretKey());
    }
}
