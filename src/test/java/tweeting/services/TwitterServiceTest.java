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
    public void testGetTweetsSuccess() throws TwitterException, BadTwitterServiceResponseException {
        ResponseList<Status> dummyList = mock(ResponseList.class);
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus);
        when(api.getHomeTimeline()).thenReturn(dummyList);

        List<Status> actualList = service.getTweets();

        verify(api).getHomeTimeline();
        assertEquals(dummyList, actualList);
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testGetTweetsServerException() throws TwitterException, BadTwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(false);
        try {
            service.getTweets();
        } catch (BadTwitterServiceResponseException e) {
            throw e;
        }
    }

    @Test
    public void testPostTweetSuccess() throws TwitterException, BadTwitterServiceResponseException, BadTwitterServiceCallException {
        Status mockedStatus = mock(Status.class);
        String dummyMessage = "some message";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Status actualStatus = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertEquals(mockedStatus, actualStatus);
    }

    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetNull() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try{
            service.postTweet(null);
        } catch (BadTwitterServiceCallException e) {
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetBlank() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try{
            service.postTweet("");
        } catch (BadTwitterServiceCallException e) {
            throw e;
        }
    }


    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetTooLong() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH + 1; i++) {
                sb.append("a"); // single character
            }
            service.postTweet(sb.toString());
        } catch (BadTwitterServiceCallException e) {
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testPostTweetServerException() throws TwitterException, BadTwitterServiceResponseException,
            BadTwitterServiceCallException {
        TwitterException te = mock(TwitterException.class);
        when(api.updateStatus(anyString())).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.postTweet("some message");
        } catch (BadTwitterServiceResponseException e) {
            throw e;
        }
    }
}
