package tweeting.resources;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TwitterResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    TwitterResource resource;

    // Dummy variables to test with
    String dummyKeyword;

    @Before
    public void setUp() {
        dummyKeyword = "keyword";
        service = mock(TwitterService.class);
        resource = new TwitterResource(service); // Use the Mocked service instead of Twitter4J impl.
    }

    @Test
    public void testTweetSuccess() throws TwitterServiceResponseException, TwitterServiceCallException {
        String message = "No Twitter Exception";
        Tweet tweet = new Tweet();

        when(service.postTweet(any())).thenReturn(Optional.of(tweet));

        Response response = resource.postTweet(message);

        verify(service).postTweet(message);
        assertNotNull(response);
        System.out.println(response.getStatus());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(tweet, response.getEntity());
    }

    @Test
    public void testTweetCallException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetResponseException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String dummyMessage = "some message";
        String dummyErrorMessage = "some error message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetGeneralException() throws TwitterServiceResponseException, TwitterServiceCallException {
        String message = "Twitter Exception";
        RuntimeException dummyException = new RuntimeException();

        when(service.postTweet(any())).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(message);

        verify(service).postTweet(message);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

    @Test
    public void testTimelineSuccess() throws TwitterServiceResponseException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);

        when(service.getHomeTimeline()).thenReturn(Optional.of(dummyList));

        Response response = resource.getHomeTimeline();

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getFilteredHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testTimelineResponseException() throws TwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTimelineCallException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }


    @Test
    public void testFilterSuccess() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);

        when(service.getFilteredTimeline(dummyKeyword)).thenReturn(Optional.of(dummyList));

        Response response = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);

        assertNotNull(response);
        TestCase.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        TestCase.assertEquals(dummyList, response.getEntity());
    }

    @Test
    public void testFilterEmptyOptional() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        when(service.getFilteredTimeline(dummyKeyword)).thenReturn(Optional.empty());

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        Assert.assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterCallException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyErrorMessage = "some message";
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        Assert.assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterResponseException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        Assert.assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        Assert.assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

}
