package tweeting.resources;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceCallException;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterHomeTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    FilterHomeTimelineResource filterResource;

    // Dummy variables to test with
    String dummyKeyword;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        filterResource = new FilterHomeTimelineResource(service); // Mock service instead of Twitter4J impl.
        dummyKeyword = "some keyword filter";
    }

    @Test
    public void testFilterSuccess() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);

        when(service.getFilteredTimeline(dummyKeyword)).thenReturn(Optional.of(dummyList));

        Response response = filterResource.getHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);

        assertNotNull(response);
        TestCase.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        TestCase.assertEquals(dummyList, response.getEntity());
    }

    @Test
    public void testFilterEmptyOptional() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        when(service.getFilteredTimeline(dummyKeyword)).thenReturn(Optional.empty());

        Response actualResponse = filterResource.getHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterCallException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyErrorMessage = "some message";
        TwitterServiceCallException dummyException = new TwitterServiceCallException(dummyErrorMessage);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterResponseException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException,
            TwitterServiceCallException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getFilteredTimeline(dummyKeyword)).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimeline(dummyKeyword);

        verify(service).getFilteredTimeline(dummyKeyword);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

}
