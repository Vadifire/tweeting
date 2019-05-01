package main;

import main.resources.GetTimelineResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import twitter4j.*;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
public class TimelineTest {

    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        timelineResource = mock(GetTimelineResource.class); // Resource to be tested

        // Partial Mock Warn: https://static.javadoc.io/org.mockito/mockito-core/2.27.0/org/mockito/Mockito.html#16
        when(timelineResource.getTweets()).thenCallRealMethod(); // Real method to be tested
    }

    @Test
    public void testSuccess() throws TwitterException {

        List<Status> dummyList = new LinkedList<Status>(); // Dummy linked list to return from getHomeTimeline()

        when(timelineResource.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testAuthFail() throws TwitterException {

        Exception dummyCause = new Exception();
        TwitterException authException = new TwitterException("Dummy String", dummyCause,
                TwitterErrorCode.AUTH_FAIL.getValue());

        when(timelineResource.getHomeTimeline()).thenThrow(authException);

        Response response = timelineResource.getTweets();

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testNetworkIssue() throws TwitterException {

        IOException networkCause = new IOException(); // Twitter4J considers IO Exceptions as network-caused
        TwitterException networkException = new TwitterException("Dummy String", networkCause, 0);

        when(timelineResource.getHomeTimeline()).thenThrow(networkException);

        Response response = timelineResource.getTweets();

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testOtherServerError() throws TwitterException {

        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        when(timelineResource.getHomeTimeline()).thenThrow(dummyException);

        Response response = timelineResource.getTweets();

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }


}
