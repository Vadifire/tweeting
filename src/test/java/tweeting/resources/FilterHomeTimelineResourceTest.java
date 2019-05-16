package tweeting.resources;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.services.TwitterService;
import tweeting.services.TwitterServiceResponseException;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterHomeTimelineResourceTest {

    // Mocked classes
    TwitterService service;

    // Resource under test
    FilterHomeTimelineResource filterResource;

    // Dummy variables to test with
    String repeated;
    List<Tweet> dummyList;
    Tweet[] tweets = new Tweet[2]; // Tweet[i+i] has message of Tweet[i] + repeated String.

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        filterResource = new FilterHomeTimelineResource(service); // Mock service instead of Twitter4J impl.

        repeated = "a";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tweets.length; i++) {
            sb.append("a");
            Tweet tweet = new Tweet();
            tweet.setMessage(sb.toString());
            tweets[i] = tweet;
        }
        dummyList = Arrays.asList(tweets);
    }

    @Test
    public void testMissingFilterParam() throws TwitterServiceResponseException {
        Response actualResponse = filterResource.getHomeTimeline(Optional.ofNullable(null));

        assertNotNull(actualResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actualResponse.getStatus());
        assertEquals(FilterHomeTimelineResource.MISSING_FILTER_MESSAGE, actualResponse.getEntity());
    }

    @Test
    public void testFilterAllResults() throws TwitterServiceResponseException {
        when(service.getHomeTimeline()).thenReturn(dummyList);

        Response actualResponse = filterResource.getHomeTimeline(Optional.of(tweets[0].getMessage()));

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        assertNotNull(actualResponse);
        assertEquals(Response.Status.OK.getStatusCode(), actualResponse.getStatus()); // Verify correct response code
        List<Tweet> filteredList = (List<Tweet>)actualResponse.getEntity();
        assertEquals(tweets.length, filteredList.size());
        assertTrue(filteredList.containsAll(dummyList));
    }

    @Test
    public void testFilterOneResult() throws TwitterServiceResponseException {
        when(service.getHomeTimeline()).thenReturn(dummyList);

        Response actualResponse = filterResource.getHomeTimeline(Optional.of(tweets[tweets.length-1].getMessage()));

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        assertNotNull(actualResponse);
        assertEquals(Response.Status.OK.getStatusCode(), actualResponse.getStatus()); // Verify correct response code
        List<Tweet> filteredList = (List<Tweet>)actualResponse.getEntity();
        assertEquals(1, filteredList.size());
        assertTrue(filteredList.contains(tweets[tweets.length-1]));
    }

    @Test
    public void testFilterNoResults() throws TwitterServiceResponseException {
        when(service.getHomeTimeline()).thenReturn(dummyList);

        Response actualResponse = filterResource.getHomeTimeline(Optional.of(tweets[tweets.length-1].getMessage() +
                repeated));

        verify(service).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()
        assertNotNull(actualResponse);
        assertEquals(Response.Status.OK.getStatusCode(), actualResponse.getStatus()); // Verify correct response code
        List<Tweet> filteredList = (List<Tweet>)actualResponse.getEntity();
        assertEquals(0, filteredList.size());
    }

    @Test
    public void testFilterServerException() throws TwitterServiceResponseException {
        String dummyErrorMessage = "some message";
        TwitterServiceResponseException dummyException = new TwitterServiceResponseException(dummyErrorMessage,
                null);

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimeline(Optional.of("some message"));

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus());
        assertEquals(dummyErrorMessage, actualResponse.getEntity().toString());
    }

    @Test
    public void testFilterGeneralException() throws TwitterServiceResponseException {
        RuntimeException dummyException = new RuntimeException();

        when(service.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = filterResource.getHomeTimeline(Optional.of("some message"));

        verify(service).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, actualResponse.getEntity().toString());
    }

}
