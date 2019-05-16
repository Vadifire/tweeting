package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TweetResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource to test
    TweetResource tweetResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);

        tweetResource = new TweetResource(service); //Use the Mocked service instead of the usual Twitter4J impl
    }

    @Test
    public void testTweetSuccess() throws TwitterServiceResponseException, TwitterServiceCallException {
        String message = "No Twitter Exception";
        Tweet tweet = new Tweet();

        when(service.postTweet(any())).thenReturn(tweet);

        Response response = tweetResource.postTweet(Optional.of(message)); // Simple valid message case

        verify(service).postTweet(Optional.of(message));
        assertNotNull(response);
        System.out.println(response.getStatus());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(tweet, response.getEntity());
    }

    @Test
    public void testTweetClientException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(Optional.of(dummyMessage));

        verify(service).postTweet(Optional.of(dummyMessage));
        assertNotNull(actualResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetServerException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(Optional.of(dummyMessage));

        verify(service).postTweet(Optional.of(dummyMessage));
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetGeneralException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String message = "Twitter Exception";
        RuntimeException dummyException = new RuntimeException();

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(Optional.of(message));

        verify(service).postTweet(Optional.of(message));
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

}
