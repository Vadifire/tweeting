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

public class UserTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    UserTimelineResource filterResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        filterResource = new UserTimelineResource(service); // Mock service instead of Twitter4J impl.
    }

    @Test
    public void testFilterSuccess() throws TwitterServiceResponseException {
        LinkedList<Tweet> dummyList = new LinkedList<>();
        Tweet dummyTweet = new Tweet();
        dummyTweet.setMessage("dummy message");
        dummyList.add(dummyTweet);

        when(service.getUserTimeline()).thenReturn(dummyList);

        Response response = filterResource.getUserTimeline("");

        verify(service).getUserTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testFilterServerException() throws TwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getUserTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getUserTimeline("");

        verify(service).getUserTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getUserTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getUserTimeline("");

        verify(service).getUserTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(),
                actualResponse.getEntity().toString());
    }


}
