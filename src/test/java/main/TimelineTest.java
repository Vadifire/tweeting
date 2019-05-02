package main;

import main.resources.GetTimelineResource;
import main.twitter.TwitterAPIWrapper;
import main.twitter.TwitterErrorCode;
import org.junit.Before;
import org.junit.Test;
import twitter4j.*;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class TimelineTest {

    GetTimelineResource timelineResource;
    TwitterAPIWrapper api;

    @Before
    public void setUp() {
        api = mock(TwitterAPIWrapper.class);
        timelineResource = new GetTimelineResource(api); //Use the Mocked API instead of the usual TwitterAPIImpl.

    }

    @Test
    public void testSuccess() throws TwitterException {

        List<Status> dummyList = new LinkedList<Status>(); // Dummy linked list to return from getHomeTimeline()

        when(api.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually retrieved something

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testAuthFail() throws TwitterException {

        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                TwitterErrorCode.AUTH_FAIL.getValue());

        when(api.getHomeTimeline()).thenThrow(authException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually retrieved something

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testNetworkIssue() throws TwitterException {

        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);

        when(api.getHomeTimeline()).thenThrow(networkException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually retrieved something

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(api.getHomeTimeline()).thenThrow(dummyException);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually retrieved something

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
