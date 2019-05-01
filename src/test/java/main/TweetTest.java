package main;

import main.resources.PostTweetResource;
import org.junit.Before;
import org.junit.Test;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TweetTest {

    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        tweetResource = mock(PostTweetResource.class); // Resource to be tested

        // Partial Mock Warn: https://static.javadoc.io/org.mockito/mockito-core/2.27.0/org/mockito/Mockito.html#16
        when(tweetResource.postTweet(any())).thenCallRealMethod();
    }

    @Test
    public void testValid() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(tweetResource.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet("No Twitter Exception"); // Simple valid message case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
    }


    @Test
    public void testNullCase() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(tweetResource.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(null); // Null test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testMessageLength() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(tweetResource.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(null); // Null test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        response = tweetResource.postTweet(""); //0 length test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

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
    }

    @Test
    public void testAuthFail() throws TwitterException {

        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                TwitterErrorCode.AUTH_FAIL.getValue());

        when(tweetResource.updateStatus(any())).thenThrow(authException);

        Response response = tweetResource.postTweet("Auth Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testNetworkIssue() throws TwitterException {

        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);

        when(tweetResource.updateStatus(any())).thenThrow(networkException);

        Response response = tweetResource.postTweet("Network Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(tweetResource.updateStatus(any())).thenThrow(dummyException);

        Response response = tweetResource.postTweet("Other Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}
