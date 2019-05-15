package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.services.BadTwitterServiceResponseException;
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
    public void testTimelineSuccess() throws BadTwitterServiceResponseException {
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy list to return from getTweets()
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus); // Populate list with mocked Status

        when(service.getTweets()).thenReturn(dummyList);

        Response response = timelineResource.getTweets();

        verify(service).getTweets(); // Verify we have actually made the call to getTweets()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testTimelineServerException() throws BadTwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        BadTwitterServiceResponseException dummyException = new BadTwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getTweets()).thenThrow(dummyException);

        Response actualResponse = timelineResource.getTweets();

        verify(service).getTweets();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testTimelineGeneralException() throws BadTwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getTweets()).thenThrow(dummyException);

        Response actualResponse = timelineResource.getTweets();

        verify(service).getTweets();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(GetTimelineResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }

}
