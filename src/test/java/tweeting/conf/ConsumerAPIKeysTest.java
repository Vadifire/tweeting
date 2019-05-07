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
        consumerAPIKeys.setApiKey(dummyKey);
        assertEquals(dummyKey, consumerAPIKeys.getApiKey());
    }

    @Test
    public void testGetApiSecretKey() {
        String dummySecret = "some secret";
        consumerAPIKeys.setApiSecretKey(dummySecret);
        assertEquals(dummySecret, consumerAPIKeys.getApiSecretKey());
    }
}
