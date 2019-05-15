package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.util.ResponseUtil;
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
    public void testGetInstanceWithConfig() {
        String consumerKey = "consumer key";
        String consumerSecret = "consumer secret";
        String token = "token";
        String tokenSecret = "token secret";
        TwitterOAuthCredentials auth = new TwitterOAuthCredentials();
        auth.setConsumerAPIKey(consumerKey);
        auth.setConsumerAPISecretKey(consumerSecret);
        auth.setAccessToken(token);
        auth.setAccessTokenSecret(tokenSecret);
        service = TwitterService.getInstance(auth);

        Twitter apiCreated = service.getAPI();

        assertEquals(consumerKey, apiCreated.getConfiguration().getOAuthConsumerKey());
        assertEquals(consumerSecret, apiCreated.getConfiguration().getOAuthConsumerSecret());
        assertEquals(token, apiCreated.getConfiguration().getOAuthAccessToken());
        assertEquals(tokenSecret, apiCreated.getConfiguration().getOAuthAccessTokenSecret());
    }

    @Test
    public void testGetTweetsSuccess() throws TwitterException, BadTwitterServiceResponseException {
        ResponseList<Status> dummyList = mock(ResponseList.class);
        Status mockedStatus = mock(Status.class);
        dummyList.add(mockedStatus);
        when(api.getHomeTimeline()).thenReturn(dummyList);

        List<Tweet> actualList = service.getHomeTimeline();

        verify(api).getHomeTimeline();
        assertEquals(dummyList, actualList);
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testGetTweetsServerException() throws TwitterException, BadTwitterServiceResponseException {
        String errorMessage = "some error message";
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getHomeTimeline();
        } catch (BadTwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testGetTweetsNetworkException() throws TwitterException, BadTwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getHomeTimeline();
        } catch (BadTwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testGetTweetsBadAuthException() throws TwitterException, BadTwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());
        try {
            service.getHomeTimeline();
        } catch (BadTwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testGetTweetsCouldNotAuthException() throws TwitterException, BadTwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());
        try {
            service.getHomeTimeline();
        } catch (BadTwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test
    public void testPostTweetSuccess() throws TwitterException, BadTwitterServiceResponseException,
            BadTwitterServiceCallException {
        Status mockedStatus = mock(Status.class);
        String dummyMessage = "some message";

        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Tweet actualTweet = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertEquals(mockedStatus, actualTweet);
    }

    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetNull() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try {
            service.postTweet(null);
        } catch (BadTwitterServiceCallException e) {
            assertEquals(ResponseUtil.getNullTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetBlank() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try {
            service.postTweet("");
        } catch (BadTwitterServiceCallException e) {
            assertEquals(ResponseUtil.getInvalidTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceCallException.class)
    public void testPostTweetTooLong() throws BadTwitterServiceResponseException, BadTwitterServiceCallException {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH + 1; i++) {
                sb.append("a"); // single character
            }
            service.postTweet(sb.toString());
        } catch (BadTwitterServiceCallException e) {
            assertEquals(ResponseUtil.getInvalidTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = BadTwitterServiceResponseException.class)
    public void testPostTweetServerException() throws TwitterException, BadTwitterServiceResponseException,
            BadTwitterServiceCallException {
        String errorMessage = "some error message";
        TwitterException te = mock(TwitterException.class);
        when(api.updateStatus(anyString())).thenThrow(te);
        when(te.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.postTweet("some message");
        } catch (BadTwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }
}
