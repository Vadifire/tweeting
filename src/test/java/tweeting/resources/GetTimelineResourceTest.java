package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;


public class GetTimelineResourceTest {

    // Mocked classes
    Twitter api;
    TwitterExceptionHandler exceptionHandler;

    // Resource under test
    GetTimelineResource timelineResource;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        exceptionHandler = mock(TwitterExceptionHandler.class);

        timelineResource = new GetTimelineResource(api); // Use the Mocked API instead of the usual TwitterAPIImpl.
        timelineResource.setExceptionHandler(exceptionHandler); // Ensure no dependency

    }

    @Test
    public void testTimelineSuccess() throws TwitterException {
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy linked list to return from getHomeTimeline()
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus); // Populate list with mocked Status

        when(api.getHomeTimeline()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testTimelineNullResponse() throws TwitterException {
        when(api.getHomeTimeline()).thenReturn(null);

        Response response = timelineResource.getTweets();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus()); // Verify code
        assertEquals(ResponseUtil.getNullResponseErrorMessage(GetTimelineResource.ATTEMPTED_ACTION),
                response.getEntity().toString());
    }

}
