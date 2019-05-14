package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.util.CharacterUtil;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class TwitterServiceTest {

    // Mocked classes
    Twitter api;

    // Class under test
    TwitterService service;

    @Before
    public void setUp() {
        service = TwitterService.getInstance();
        api = mock(Twitter.class);
        service.setAPI(api);
    }

    @Test
    public void testGetTweetsSuccess() throws TwitterException, TwitterServiceException {
        ResponseList<Status> dummyList = mock(ResponseList.class);
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus);
        when(api.getHomeTimeline()).thenReturn(dummyList);

        List<Status> actualList = service.getTweets();

        verify(api).getHomeTimeline();
        assertEquals(dummyList, actualList);
    }

    @Test(expected = TwitterServiceException.class)
    public void testGetTweetsServiceException() throws TwitterException, TwitterServiceException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(false);
        try {
            service.getTweets();
        } catch (TwitterServiceException e) {
            assertFalse(e.isCausedByNetworkIssue());
            throw e;
        }
    }

    @Test(expected = TwitterServiceException.class)
    public void testGetTweetsNetworkException() throws TwitterException, TwitterServiceException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getTweets();
        } catch (TwitterServiceException e) {
            assertTrue(e.isCausedByNetworkIssue());
            throw e;
        }
    }

    @Test
    public void testPostTweetSuccess() throws TwitterException, TwitterServiceException {
        Status mockedStatus = mock(Status.class);
        String dummyMessage = "some message";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Status actualStatus = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertEquals(mockedStatus, actualStatus);
    }

    @Test(expected = TwitterServiceException.class)
    public void testPostTweetNull() throws TwitterServiceException {
        try{
            service.postTweet(null);
        } catch (TwitterServiceException e) {
            assertTrue(e.isCausedByNullParam());
            throw e;
        }
    }

    @Test(expected = TwitterServiceException.class)
    public void testPostTweetBlank() throws TwitterServiceException {
        try{
            service.postTweet("");
        } catch (TwitterServiceException e) {
            assertEquals(TwitterErrorCode.MESSAGE_BLANK.getCode(), e.getErrorCode());
            throw e;
        }
    }


    @Test(expected = TwitterServiceException.class)
    public void testPostTweetTooLong() throws TwitterServiceException {
        try{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH + 1; i++) {
                sb.append("a"); // single character
            }
            service.postTweet(sb.toString());
        } catch (TwitterServiceException e) {
            assertEquals(TwitterErrorCode.MESSAGE_TOO_LONG.getCode(), e.getErrorCode());
            throw e;
        }
    }

    @Test(expected = TwitterServiceException.class)
    public void testPostTweetServiceException() throws TwitterException, TwitterServiceException {
        TwitterException te = mock(TwitterException.class);
        when(api.updateStatus(anyString())).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(false);
        try {
            service.postTweet("some message");
        } catch (TwitterServiceException e) {
            throw e;
        }
    }

    @Test(expected = TwitterServiceException.class)
    public void testPostTweetNetworkException() throws TwitterException, TwitterServiceException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getTweets();
        } catch (TwitterServiceException e) {
            assertTrue(e.isCausedByNetworkIssue());
            throw e;
        }
    }

}
