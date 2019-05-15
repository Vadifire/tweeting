package tweeting.resources;

import tweeting.services.BadTwitterServiceResponseException;
import tweeting.services.BadTwitterServiceCallException;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import twitter4j.Status;

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
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        mockedStatus = mock(Status.class);

        tweetResource = new PostTweetResource(service); //Use the Mocked service instead of the usual Twitter4J impl
    }

    @Test
    public void testTweetSuccess() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        String message = "No Twitter Exception";

        when(service.postTweet(anyString())).thenReturn(mockedStatus);

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(service).postTweet(message);
        assertNotNull(response);
        System.out.println(response.getStatus());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetClientException() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        BadTwitterServiceCallException dummyException = new BadTwitterServiceCallException(dummyErrorMessage);

        when(service.postTweet(anyString())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetServerException() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        BadTwitterServiceResponseException dummyException = new BadTwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.postTweet(anyString())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetGeneralException() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        String message = "Twitter Exception";
        RuntimeException dummyException = new RuntimeException();

        when(service.postTweet(anyString())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(message);

        verify(service).postTweet(message);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(),
                actualResponse.getEntity().toString());
    }

}
