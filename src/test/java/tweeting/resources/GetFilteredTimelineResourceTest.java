package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceResponseException;
import tweeting.util.ResponseUtil;

import javax.ws.rs.core.Response;
import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFilteredTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    GetFilteredTimelineResource filterResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        filterResource = new GetFilteredTimelineResource(service); // Mock service instead of Twitter4J impl.
    }

    @Test
    public void testFilterSuccess() throws TwitterServiceResponseException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyList.add(dummyTweet);

        when(service.getHomeTimeline()).thenReturn(dummyList);

        Response response = filterResource.getHomeTimelineFiltered("");

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testFilterServerException() throws TwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimelineFiltered("");

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimelineFiltered("");

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(),
                actualResponse.getEntity().toString());
    }


}
