package main;

import main.resources.PostTweetResource;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class TweetTest {

    // Mocked classes
    Twitter api;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
        mockedStatus = mock(Status.class);
    }

    @Test
    public void testTweetValid() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(message); // Simple valid message case
        verify(api).updateStatus(message);
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.SUCCESS.getValue(message));
    }


    @Test
    public void testTweetNullCase() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(null); // Null test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.NULL_MESSAGE.getValue());
    }

    @Test
    public void testTweetZeroLength() throws TwitterException {
        when(api.updateStatus(anyString())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(""); //0 length test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.TOO_SHORT_MESSAGE.getValue());
    }

    @Test
    public void testTweetMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < PostTweetResource.MAX_TWEET_LENGTH; i++) {
            sb.append("a"); // single character
        }
        Response response = tweetResource.postTweet(sb.toString()); // Max length test case
        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testTweetTooLong() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < PostTweetResource.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }
        Response response = tweetResource.postTweet(sb.toString());
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.TOO_LONG_MESSAGE.getValue());
    }


    @Test
    public void testTweetAuthFail() throws TwitterException {

        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                Response.Status.UNAUTHORIZED.getStatusCode());
        String message = "Auth Check";

        when(api.updateStatus(anyString())).thenThrow(authException);

        Response response = tweetResource.postTweet(message);

        verify(api).updateStatus(message); // Verify that updateStatus has been called correctly.

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.AUTH_FAIL.getValue());
    }

    @Test
    public void testTweetNetworkIssue() throws TwitterException {

        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);
        String message = "Network Check";

        when(api.updateStatus(anyString())).thenThrow(networkException);

        Response response = tweetResource.postTweet(message);

        verify(api).updateStatus(message); // Verify that updateStatus has been called correctly.

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.NETWORK_ISSUE.getValue());
    }

    @Test
    public void testTweetOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);
        String message = "Other Check";

        when(api.updateStatus(anyString())).thenThrow(dummyException);

        Response response = tweetResource.postTweet(message);

        verify(api).updateStatus(message); // Verify that updateStatus has been called correctly.

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(),
                PostTweetResource.ResponseMessage.OTHER_ERROR.getValue(dummyException.getErrorMessage()));
    }
}
