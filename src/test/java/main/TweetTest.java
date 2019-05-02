package main;

import main.resources.PostTweetResource;
import main.twitter.TwitterAPIWrapper;

import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TweetTest {

    TwitterAPIWrapper api;
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        api = mock(TwitterAPIWrapper.class);
        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
    }

    @Test
    public void testTweetValid() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet("No Twitter Exception"); // Simple valid message case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
    }


    @Test
    public void testTweetNullCase() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(null); // Null test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.NULL_MESSAGE.getValue());
    }

    @Test
    public void testTweetMessageLength() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(""); //0 length test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.TOO_SHORT_MESSAGE.getValue());

        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < 280; i++) {
            sb.append("a"); // single character
        }
        response = tweetResource.postTweet(sb.toString()); // Exactly 280 test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());

        sb.append("a"); // Over 280 characters
        response = tweetResource.postTweet(sb.toString());
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.TOO_LONG_MESSAGE.getValue());
    }

    @Test
    public void testTweetAuthFail() throws TwitterException {

        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                Response.Status.UNAUTHORIZED.getStatusCode());

        when(api.updateStatus(any())).thenThrow(authException);

        String message = "Auth Check";
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

        when(api.updateStatus(any())).thenThrow(networkException);

        String message = "Network Check";
        Response response = tweetResource.postTweet(message);

        verify(api).updateStatus(message); // Verify that updateStatus has been called correctly.

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.NETWORK_ISSUE.getValue());
    }

    @Test
    public void testTweetOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(api.updateStatus(any())).thenThrow(dummyException);

        String message = "Other Check";
        Response response = tweetResource.postTweet(message);

        verify(api).updateStatus(message); // Verify that updateStatus has been called correctly.

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
