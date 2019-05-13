package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tweeting.resources.GetTimelineResource;
import tweeting.resources.PostTweetResource;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterErrorCode;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.util.CharacterUtil;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;

public class TwitterServiceTest {


    // Mocked classes
    Twitter api;
    Status mockedStatus;
    TwitterException mockedTwitterException;

    String attemptedAction;

    // Class under test
    TwitterService twitterService;

    @Before
    public void setUp() {
        api = mock(Twitter.class);
        mockedStatus = mock(Status.class);
        mockedTwitterException = mock(TwitterException.class);
        attemptedAction = "some action";

        twitterService = Mockito.spy(TwitterService.getInstance());
        twitterService.setTwitterAPI(api); // Use the Mocked API instead of the usual TwitterAPIImpl.
    }

    @Test
    public void testTimelineSuccess() throws TwitterException {
        ResponseList<Status> dummyList = mock(ResponseList.class); // Dummy linked list to return from getHomeTimeline()
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus); // Populate list with mocked Status

        when(api.getHomeTimeline()).thenReturn(dummyList);

        Response response = twitterService.getHomeTimeline();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus()); // Verify correct response code
        assertEquals(dummyList, response.getEntity()); // Verify correct content
    }

    @Test
    public void testTimelineNullResponse() throws TwitterException {
        when(api.getHomeTimeline()).thenReturn(null);

        Response response = twitterService.getHomeTimeline();

        verify(api).getHomeTimeline(); // Verify we have actually made the call to getHomeTimeline()

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus()); // Verify code
        assertEquals(ResponseUtil.getNullResponseErrorMessage(GetTimelineResource.ATTEMPTED_ACTION),
                response.getEntity().toString());
    }

    @Test
    public void testTimelineTwitterException() throws TwitterException {
        // This method exclusively tests that catchTwitterException has been called.
        // Separate testing is done to ensure catchTwitterException behaves as expected.
        when(api.getHomeTimeline()).thenThrow(mockedTwitterException);

        twitterService.getHomeTimeline();

        verify(api).getHomeTimeline();
        verify(twitterService).catchTwitterException(mockedTwitterException, GetTimelineResource.ATTEMPTED_ACTION);
    }

    @Test
    public void testTimelineGeneralException() throws TwitterException {
        RuntimeException dummyException = mock(RuntimeException.class);

        when(api.getHomeTimeline()).thenThrow(dummyException);

        Response actualResponse = twitterService.getHomeTimeline();

        verify(api).getHomeTimeline();
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(GetTimelineResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }

    @Test
    public void testTweetValid() throws TwitterException {
        String message = "No TwitterServiceTest Exception";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Response response = twitterService.postTweet(message); // Simple valid message case

        verify(api).updateStatus(message);
        assertNotNull(response);
        System.out.println(response.getStatus());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetNullCase() {
        Response response = twitterService.postTweet(null); // Null test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getNullParamErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM), response.getEntity().toString());
    }

    @Test
    public void testTweetZeroLength() {
        String message = "";

        Response response = twitterService.postTweet(message); // 0 length test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.PARAM_UNIT,
                CharacterUtil.MAX_TWEET_LENGTH), response.getEntity().toString());
    }

    @Test
    public void testTweetMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH; i++) {
            sb.append("a"); // single character
        }

        when(api.updateStatus(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = twitterService.postTweet(sb.toString()); // Max length test case

        verify(api).updateStatus(sb.toString());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH + 1; i++) {
            sb.append("a"); // single character
        }

        Response response = twitterService.postTweet(sb.toString());

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.PARAM_UNIT, CharacterUtil.MAX_TWEET_LENGTH),
                response.getEntity().toString());
    }

    @Test
    public void testTweetTwitterException() throws TwitterException {
        // This method exclusively tests that catchTwitterException has been called.
        // Separate testing is done to ensure catchTwitterException behaves as expected.

        String dummyMessage = "dummy message";
        when(api.updateStatus(dummyMessage)).thenThrow(mockedTwitterException);

        twitterService.postTweet(dummyMessage);

        verify(api).updateStatus(dummyMessage);
        verify(twitterService).catchTwitterException(mockedTwitterException, PostTweetResource.ATTEMPTED_ACTION);
    }

    @Test
    public void testTweetGeneralException() throws TwitterException {
        String message = "Some TwitterServiceTest Exception";
        RuntimeException dummyException = mock(RuntimeException.class);

        when(api.updateStatus(anyString())).thenThrow(dummyException);

        Response actualResponse = twitterService.postTweet(message);

        verify(api).updateStatus(message);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(PostTweetResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }


    @Test
    public void testBadAuthError() {
        when(mockedTwitterException.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());

        Response response = twitterService.catchTwitterException(mockedTwitterException, attemptedAction);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testCouldNotAuthError() {
        when(mockedTwitterException.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());

        Response response = twitterService.catchTwitterException(mockedTwitterException, attemptedAction);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testNetworkError() {
        when(mockedTwitterException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedTwitterException.isCausedByNetworkIssue()).thenReturn(true); // Don't rely on Twitter4J impl.

        Response response = twitterService.catchTwitterException(mockedTwitterException, attemptedAction);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getNetworkErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testOtherError() {
        String dummyCauseMessage = "Some error cause";

        when(mockedTwitterException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedTwitterException.isCausedByNetworkIssue()).thenReturn(false); // Don't rely on Twitter4J impl.
        when(mockedTwitterException.getErrorMessage()).thenReturn(dummyCauseMessage);

        Response response = twitterService.catchTwitterException(mockedTwitterException, attemptedAction);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getOtherErrorMessage(attemptedAction, dummyCauseMessage),
                response.getEntity().toString());
    }

    @Test
    public void testGeneralException() {
        RuntimeException dummyRuntimeException = mock(RuntimeException.class);

        when(mockedTwitterException.getErrorCode()).thenThrow(dummyRuntimeException);

        Response actualResponse = twitterService.catchTwitterException(mockedTwitterException, attemptedAction);

        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction),
                actualResponse.getEntity().toString());

    }

}
