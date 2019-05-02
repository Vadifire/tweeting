package main.resources;

import org.junit.Before;
import org.junit.Test;
import twitter4j.*;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class GetTimelineResourceTest {

    // Mocked class
    Twitter api;

    // Resource under test
    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        timelineResource = new GetTimelineResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl.
    }

    @Test
    public void testTimelineSuccess() throws TwitterException {
        Status mockedStatus = mock(Status.class);
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy linked list to return from getHomeTimeline()
        dummyList.add(mockedStatus);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        assertEquals(response.getEntity(), dummyList);
    }

    @Test
    public void testTimelineAuthFail() throws TwitterException {
        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                Response.Status.UNAUTHORIZED.getStatusCode());

        when(api.getHomeTimeline()).thenThrow(authException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(), GetTimelineResource.ResponseMessage.AUTH_FAIL.getValue());
    }

    @Test
    public void testTimelineNetworkIssue() throws TwitterException {
        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);

        when(api.getHomeTimeline()).thenThrow(networkException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(), GetTimelineResource.ResponseMessage.NETWORK_ISSUE.getValue());
    }

    @Test
    public void testTimelineOtherServerError() throws TwitterException {
        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(api.getHomeTimeline()).thenThrow(dummyException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(response.getEntity().toString(),
                GetTimelineResource.ResponseMessage.OTHER_ERROR.getValue(dummyException.getErrorMessage()));
    }

}
