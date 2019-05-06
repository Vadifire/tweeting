package tweeting.resources;

import org.junit.After;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class PostTweetResourceTest {

    // Mocked classes
    Twitter api;
    TwitterExceptionHandler exceptionHandler;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        mockedStatus = mock(Status.class);
        exceptionHandler = mock(TwitterExceptionHandler.class);

        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
        tweetResource.setExceptionHandler(exceptionHandler); // Ensure no dependency
    }

    @Test
    public void testTweetValid() throws TwitterException {
        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetNullCase() {
        Response response = tweetResource.postTweet(null); // Null test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getNullParamErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM), response.getEntity().toString());
    }

    @Test
    public void testTweetZeroLength() {
        String message = "";

        Response response = tweetResource.postTweet(message); //0 length test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.UNIT, PostTweetResource.MIN_TWEET_LENGTH,
                PostTweetResource.MAX_TWEET_LENGTH), response.getEntity().toString());
    }

    @Test
    public void testTweetMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < PostTweetResource.MAX_TWEET_LENGTH; i++) {
            sb.append("a"); // single character
        }

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(sb.toString()); // Max length test case

        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < PostTweetResource.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }

        Response response = tweetResource.postTweet(sb.toString());

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.UNIT,
                1, PostTweetResource.MAX_TWEET_LENGTH), response.getEntity().toString());
    }

    @Test
    public void testTweetException() throws TwitterException {

        // Test that postTweet() properly calls catchTwitterException() in exception case

        String message = "Some Twitter Exception";
        Response expectedResponse = mock(Response.class);
        TwitterException dummyException = mock(TwitterException.class);

        when(api.updateStatus(anyString())).thenThrow(dummyException);
        when(exceptionHandler.catchTwitterException(dummyException)).thenReturn(expectedResponse);

        Response actualResponse = tweetResource.postTweet(message);

        verify(api).updateStatus(message);
        verify(exceptionHandler).catchTwitterException(dummyException);
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }
}