package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.Twitter4JService;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TwitterResourceTest {

    // Mocked service
    private TwitterService service;

    // Resource under test
    private TwitterResource resource;

    // Dummy variables to test with
    private String dummyMessage, dummyKeyword;
    private String dummyErrorMessage; // Invariant: Error message in case of TwitterService Call/Response Exceptions
    private LinkedList<Tweet> dummyList; // Invariant: returned by getTimeline methods
    private Long dummyId;

    @Before
    public void setUp() {

        dummyMessage = "some message";
        dummyId = 123L;
        dummyErrorMessage = "some error message";
        dummyKeyword = "keyword";

        service = mock(Twitter4JService.class);
        resource = new TwitterResource(service); // Fine for single-class unit tests (https://dagger.dev/testing.html)

        dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);
    }

    /* Utility methods to reduce duplicate code */

    public void assertCallException(Response response) { // 400 Error Code signalled by service
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(dummyErrorMessage, response.getEntity().toString());
    }

    public void assertResponseException(Response response) { // 500 Error Code signalled by service
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(dummyErrorMessage, response.getEntity().toString());
    }

    public void assertGeneralException(Response response) { // 500 Error Code, Unexpected Exception
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_ERROR_MESSAGE, response.getEntity().toString());
    }

    public void assertTimelineSuccess(Response response) {
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    /* End of utility methods */

    @Test
    public void testTweetSuccess() throws TwitterServiceResponseException, TwitterServiceCallException {
        Tweet tweet = new Tweet();

        when(service.postTweet(dummyMessage)).thenReturn(Optional.of(tweet));

        Response response = resource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(tweet, response.getEntity());
    }

    @Test
    public void testTweetCallException() throws TwitterServiceResponseException, TwitterServiceCallException {
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.postTweet(dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertCallException(actualResponse);
    }

    @Test
    public void testTweetResponseException() throws TwitterServiceResponseException, TwitterServiceCallException {
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.postTweet(dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(dummyMessage);

        verify(service).postTweet(dummyMessage);
        assertResponseException(actualResponse);
    }

    @Test
    public void testTweetGeneralException() throws TwitterServiceResponseException, TwitterServiceCallException {
        RuntimeException dummyException = new RuntimeException();

        when(service.postTweet(dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.postTweet(dummyErrorMessage);

        verify(service).postTweet(dummyErrorMessage);
        assertGeneralException(actualResponse);
    }


    @Test
    public void testReplySuccess() throws TwitterServiceResponseException, TwitterServiceCallException {
        Tweet tweet = new Tweet();
        when(service.replyToTweet(dummyId, dummyMessage)).thenReturn(Optional.of(tweet));

        Response response = resource.replyToTweet(dummyId, dummyMessage);

        verify(service).replyToTweet(dummyId, dummyMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(tweet, response.getEntity());
    }

    @Test
    public void testReplyCallException() throws TwitterServiceResponseException, TwitterServiceCallException {
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.replyToTweet(dummyId, dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.replyToTweet(dummyId, dummyMessage);

        verify(service).replyToTweet(dummyId, dummyMessage);
        assertCallException(actualResponse);
    }

    @Test
    public void testReplyResponseException() throws TwitterServiceResponseException, TwitterServiceCallException {
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.replyToTweet(dummyId, dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.replyToTweet(dummyId, dummyMessage);

        verify(service).replyToTweet(dummyId, dummyMessage);
        assertResponseException(actualResponse);
    }

    @Test
    public void testReplyGeneralException() throws TwitterServiceResponseException, TwitterServiceCallException {
        RuntimeException dummyException = new RuntimeException();

        when(service.replyToTweet(dummyId, dummyMessage)).thenThrow(dummyException);

        Response actualResponse = resource.replyToTweet(dummyId, dummyErrorMessage);

        verify(service).replyToTweet(dummyId, dummyErrorMessage);
        assertGeneralException(actualResponse);
    }

    @Test
    public void testHomeTimelineSuccess() throws TwitterServiceResponseException {
        when(service.getHomeTimeline()).thenReturn(Optional.of(dummyList));

        Response actualResponse = resource.getHomeTimeline();

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        assertTimelineSuccess(actualResponse);
    }


    @Test
    public void testHomeTimelineResponseException() throws TwitterServiceResponseException {
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertResponseException(actualResponse);
    }

    @Test
    public void testHomeTimelineGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertGeneralException(actualResponse);
    }

    @Test
    public void testUserTimelineSuccess() throws TwitterServiceResponseException {
        when(service.getUserTimeline()).thenReturn(Optional.of(dummyList));

        Response actualResponse = resource.getUserTimeline();

        verify(service).getUserTimeline(); // Verify we have actually made the call to getUserTimeline()
        assertTimelineSuccess(actualResponse);
    }


    @Test
    public void testUserTimelineResponseException() throws TwitterServiceResponseException {
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getUserTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getUserTimeline();

        verify(service).getUserTimeline();
        assertResponseException(actualResponse);
    }

    @Test
    public void testUserTimelineGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getUserTimeline()).thenThrow(dummyException);

        Response actualResponse = resource.getUserTimeline();

        verify(service).getUserTimeline();
        assertGeneralException(actualResponse);
    }

    @Test
    public void testFilterSuccess() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        when(service.getFilteredTimeline(dummyKeyword)).thenReturn(Optional.of(dummyList));

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertTimelineSuccess(actualResponse);
    }


    @Test
    public void testFilterCallException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertCallException(actualResponse);
    }

    @Test
    public void testFilterResponseException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertResponseException(actualResponse);
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = resource.getFilteredHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertGeneralException(actualResponse);
    }

}
