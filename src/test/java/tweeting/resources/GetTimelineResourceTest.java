package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class GetTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Class under test
    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        timelineResource = new GetTimelineResource(service); // Use the Mocked Service instead of real impl
    }

    @Test
    public void TestGetTimeline() {
        Response dummyResponse = mock(Response.class);
        when(service.getHomeTimeline()).thenReturn(dummyResponse);

        Response actualResponse = timelineResource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(dummyResponse, actualResponse);
    }

    @Test
    public void TestGetTimelineException() {
        RuntimeException dummyException = mock(RuntimeException.class);
        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = timelineResource.getHomeTimeline();

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(GetTimelineResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }

}
