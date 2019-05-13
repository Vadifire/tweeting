package tweeting.resources;

import tweeting.services.TwitterService;
import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import tweeting.util.TwitterServiceException;
import twitter4j.Status;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import twitter4j.util.CharacterUtil;

public class PostTweetResourceTest {

    // Mocked classes
    TwitterService service;
    TwitterExceptionHandler exceptionHandler;
    Status mockedStatus;

    // Resource to test
    PostTweetResource tweetResource;

    @Before
    public void setUp() {
        service = mock(TwitterService.class);
        mockedStatus = mock(Status.class);
        exceptionHandler = mock(TwitterExceptionHandler.class);

        tweetResource = new PostTweetResource(service); //Use the Mocked service instead of the usual Twitter4J impl
        tweetResource.setExceptionHandler(exceptionHandler); // Ensure no dependency

        when(service.getMaxCharacterLength()).thenReturn(CharacterUtil.MAX_TWEET_LENGTH);
    }

    @Test
    public void testTweetValid() throws TwitterServiceException {
        String message = "No Twitter Exception";

        when(service.postTweet(anyString())).thenReturn(mockedStatus);

        Response response = tweetResource.postTweet(message); // Simple valid message case

        verify(service).postTweet(message);
        assertNotNull(response);
        System.out.println(response.getStatus());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetNullCase() {
        Response response = tweetResource.postTweet(null); // Null test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getNullParamErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM), response.getEntity().toString());
    }

    @Test
    public void testTweetZeroLength() {
        String message = "";

        Response response = tweetResource.postTweet(message); // 0 length test case

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.PARAM_UNIT,
                CharacterUtil.MAX_TWEET_LENGTH), response.getEntity().toString());
    }

    @Test
    public void testTweetMaxLength() throws TwitterServiceException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < CharacterUtil.MAX_TWEET_LENGTH; i++) {
            sb.append("a"); // single character
        }

        when(service.postTweet(anyString())).thenReturn(mockedStatus); // Return a status without TwitterException

        Response response = tweetResource.postTweet(sb.toString()); // Max length test case

        verify(service).postTweet(sb.toString());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockedStatus, response.getEntity());
    }

    @Test
    public void testTweetTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < CharacterUtil.MAX_TWEET_LENGTH+1; i++) {
            sb.append("a"); // single character
        }

        Response response = tweetResource.postTweet(sb.toString());

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getParamBadLengthErrorMessage(PostTweetResource.ATTEMPTED_ACTION,
                PostTweetResource.MESSAGE_PARAM, PostTweetResource.PARAM_UNIT, CharacterUtil.MAX_TWEET_LENGTH),
                response.getEntity().toString());
    }

    @Test
    public void testTweetTwitterException() throws TwitterServiceException {

        // Test that postTweet() properly calls catchTwitterException() in exception case

        String message = "Some Twitter Exception";
        Response expectedResponse = mock(Response.class);
        TwitterServiceException dummyException = mock(TwitterServiceException.class);

        when(service.postTweet(anyString())).thenThrow(dummyException);
        when(exceptionHandler.catchTwitterException(dummyException)).thenReturn(expectedResponse);

        Response actualResponse = tweetResource.postTweet(message);

        verify(service).postTweet(message);
        verify(exceptionHandler).catchTwitterException(dummyException);
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testTweetGeneralException() throws TwitterServiceException {
        String message = "Some Twitter Exception";
        RuntimeException dummyException = mock(RuntimeException.class);

        when(service.postTweet(anyString())).thenThrow(dummyException);

        Response actualResponse = tweetResource.postTweet(message);

        verify(service).postTweet(message);
        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(PostTweetResource.ATTEMPTED_ACTION),
                actualResponse.getEntity().toString());
    }
}
