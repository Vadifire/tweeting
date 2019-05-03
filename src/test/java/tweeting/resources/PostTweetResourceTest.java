package tweeting.resources;

import org.junit.After;
import tweeting.util.ResponseUtil;
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

    // Dummy vars
    String paramName;
    String unitName;

    // Mocked classes
    Twitter api;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    // Spy ResponseUtil to verify this unit is not dependent on catchTwitterException
    ResponseUtil resUtil;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        mockedStatus = mock(Status.class);
        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
        resUtil = spy(tweetResource.getResUtil());
        paramName = "message"; // Should enforce this exact String
        unitName = "characters"; // Should enforce this exact String
    }

    @Test
    public void testTweetValid() throws TwitterException {
        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);
        when(mockedStatus.getText()).thenReturn(message);

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        verify(resUtil, never()).catchTwitterException(any());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetNullCase() {
        Response response = tweetResource.postTweet(null); // Null test case

        verify(resUtil, never()).catchTwitterException(any());
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getNullParamErrorMessage(paramName), response.getEntity().toString());
    }

    @Test
    public void testTweetZeroLength() {
        String message = "";

        Response response = tweetResource.postTweet(message); //0 length test case

        verify(resUtil, never()).catchTwitterException(any());
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getParamBadLengthErrorMessage(paramName,
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

        verify(resUtil, never()).catchTwitterException(any());
        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < CharacterUtil.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }

        Response response = tweetResource.postTweet(sb.toString());

        verify(resUtil, never()).catchTwitterException(any());
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(tweetResource.getResUtil().getParamBadLengthErrorMessage(paramName, unitName,
                1, CharacterUtil.MAX_TWEET_LENGTH), response.getEntity().toString());
    }
}
