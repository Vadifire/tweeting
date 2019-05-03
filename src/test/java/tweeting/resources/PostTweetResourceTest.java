package tweeting.resources;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import twitter4j.util.CharacterUtil;

public class PostTweetResourceTest {

    // Mocked classes
    Twitter api;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;
    String paramName;
    String unitName;

    @Before
    public void setUp() throws TwitterException {
        api = mock(Twitter.class);
        mockedStatus = mock(Status.class);
        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
        paramName = "message"; // Should enforce this exact String
        unitName = "characters"; // Should enforce this exact String

        when(api.destroyStatus(anyInt())).thenReturn(mockedStatus); // Stub delete api call to avoid real call
    }

    @Test
    public void testTweetValid() throws TwitterException {
        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);
        when(mockedStatus.getText()).thenReturn(message);

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetNullCase() throws TwitterException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(null); // Null test case

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getNullParamError(paramName), response.getEntity().toString());
    }

    @Test
    public void testTweetZeroLength() throws TwitterException {
        String message = "";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(message); //0 length test case

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getParamBadLengthError(paramName,
                unitName, 1, CharacterUtil.MAX_TWEET_LENGTH), response.getEntity().toString());
    }

    @Test
    public void testTweetMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < CharacterUtil.MAX_TWEET_LENGTH; i++) {
            sb.append("a"); // single character
        }

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(sb.toString());

        Response response = tweetResource.postTweet(sb.toString()); // Max length test case

        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetTooLong() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < CharacterUtil.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(sb.toString());

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getParamBadLengthError(paramName, unitName,
                1, CharacterUtil.MAX_TWEET_LENGTH), response.getEntity().toString());
    }
}
