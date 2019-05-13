package tweeting.resources;

import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class PostTweetResourceTest {

    // Mocked classes
    TwitterService service;

    String dummyMessage;

    // Class under test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        dummyMessage = "some message";
        service = mock(TwitterService.class);
        tweetResource = new PostTweetResource(service); // Use the Mocked Service instead of real impl
    }

    @Test
    public void TestPostTweet() {
        Response dummyResponse = mock(Response.class);
        when(service.postTweet(anyString())).thenReturn(dummyResponse);

        Response actualResponse = tweetResource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(dummyResponse, actualResponse);
    }

    @Test
    public void TestPostTweetException(){
        String dummyMessage = "some message";
        RuntimeException dummyException = mock(RuntimeException.class);
        when(service.postTweet(anyString())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(PostTweetResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }

}
