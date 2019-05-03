package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import tweeting.util.ResponseUtil;
import twitter4j.*;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class GetTimelineResourceTest {

    // Mocked class
    Twitter api;

    // Spied class. We want to ensure TwitterException is separately unit tested.
    ResponseUtil resUtil;

    // Resource under test
    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);

        timelineResource = new GetTimelineResource(api); // Use the Mocked API instead of the usual TwitterAPIImpl.

        resUtil = spy(timelineResource.getResUtil());
    }

    @Test
    public void testTimelineSuccess() throws TwitterException {
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy linked list to return from getHomeTimeline()
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus); // Populate list with mocked Status

        when(api.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        verify(resUtil, never()).catchTwitterException(any()); // Verify no exception testing

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode()); // Verify correct response code
        assertEquals(response.getEntity(), dummyList); // Verify correct content
    }

    @Test
    public void testTimelineNullResponse() throws TwitterException {
        when(api.getHomeTimeline()).thenReturn(null);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        verify(resUtil, never()).catchTwitterException(any()); // Verify no exception testing

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); // Verify code
        assertEquals(response.getEntity().toString(), timelineResource.getResUtil().getNullResponse()); // Verify cont.
    }

}
