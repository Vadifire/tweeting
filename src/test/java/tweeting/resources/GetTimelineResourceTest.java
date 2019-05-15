package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.services.TwitterServiceResponseException;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import twitter4j.ResponseList;
import twitter4j.Status;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class GetTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);

        timelineResource = new GetTimelineResource(service); // Use the Mocked service instead of Twitter4J impl.
    }

    @Test
    public void testTimelineSuccess() throws TwitterServiceResponseException {
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy list to return from getHomeTimeline()
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus); // Populate list with mocked Status

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
