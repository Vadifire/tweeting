package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterServiceResponseException;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;

import javax.ws.rs.core.Response;

import java.util.LinkedList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class HomeTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    HomeTimelineResource timelineResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);

        timelineResource = new HomeTimelineResource(service); // Use the Mocked service instead of Twitter4J impl.
    }

    @Test
    public void testTimelineSuccess() throws TwitterServiceResponseException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);

        when(service.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getHomeTimeline();

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testTimelineServerException() throws TwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = timelineResource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTimelineGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = timelineResource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(),
                actualResponse.getEntity().toString());
    }

}
