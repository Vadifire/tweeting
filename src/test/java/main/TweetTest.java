package main;

import main.resources.PostTweetResource;
import main.twitter.TwitterAPIWrapper;
import main.twitter.TwitterErrorCode;

import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TweetTest {

    TwitterAPIWrapper api;
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        api = mock(TwitterAPIWrapper.class);
        tweetResource = new PostTweetResource() { //Return the Mocked API instead of the usual TwitterAPIImpl.
            @Override
            protected TwitterAPIWrapper getApi() {
                return api;
            }
        };
    }

    @Test
    public void testValid() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet("No Twitter Exception"); // Simple valid message case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
    }


    @Test
    public void testNullCase() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

        Response response = tweetResource.postTweet(null); // Null test case
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testMessageLength() throws TwitterException {

        // updateStatus() can return anything that isn't a TwitterException and getTimeline() should return a
        // successful response.

        when(api.updateStatus(any())).thenReturn(null); // anything but Twitter Exception

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

        when(api.updateStatus(any())).thenThrow(authException);

        Response response = tweetResource.postTweet("Auth Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testNetworkIssue() throws TwitterException {

        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);

        when(api.updateStatus(any())).thenThrow(networkException);

        Response response = tweetResource.postTweet("Network Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(api.updateStatus(any())).thenThrow(dummyException);

        Response response = tweetResource.postTweet("Other Check");

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }
}