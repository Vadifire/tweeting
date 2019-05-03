package tweeting.resources;

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

public class PostTweetResourceTest {

    // Mocked classes
    Twitter api;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        mockedStatus = mock(Status.class);
        tweetResource = new PostTweetResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl
    }

    @Test
    public void testTweetValid() throws TwitterException {
        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(message); // Return successful update's text

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.SUCCESS.getValue(message));
    }

    @Test
    public void testTweetFailedToUpdate() throws TwitterException {
        String message = "No Twitter Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(message + " Twitter failed to update"); // Not correct message

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(),
                PostTweetResource.ResponseMessage.FAILED_UPDATE.getValue());
    }

    @Test
    public void testTweetNullCase() throws TwitterException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(null);

        Response response = tweetResource.postTweet(null); // Null test case

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity().toString(), PostTweetResource.ResponseMessage.NULL_MESSAGE.getValue());
    }

    @Test
    public void testTweetZeroLength() throws TwitterException {
        String message = "";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(message); // Return successful update's text

        Response response = tweetResource.postTweet(message); //0 length test case

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
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

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(sb.toString()); // Return successful update's text

        Response response = tweetResource.postTweet(sb.toString()); // Max length test case

        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        assertEquals(response.getEntity().toString(),
                PostTweetResource.ResponseMessage.SUCCESS.getValue(sb.toString()));
    }

    @Test
    public void testTweetTooLong() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < PostTweetResource.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException
        when(mockedStatus.getText()).thenReturn(sb.toString()); // Return successful update's text

        Response response = tweetResource.postTweet(sb.toString());

        verify(api, never()).updateStatus(anyString()); // Make sure not to call updateStatus()
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
