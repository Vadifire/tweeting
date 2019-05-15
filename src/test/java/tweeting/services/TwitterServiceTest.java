package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.util.ResponseUtil;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.util.CharacterUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class TwitterServiceTest {

    // Mocked classes
    Twitter api;
    Status mockedStatus;
    User mockedUser;

    // Dummy vars
    String dummyName;
    String dummyScreenName;
    String dummyURL;
    String dummyMessage;
    Date dummyDate;

    // Class under test
    TwitterService service;

    @Before
    public void setUp() {
        mockedStatus = mock(Status.class);
        mockedUser = mock(User.class);
        dummyMessage = "some message";

        dummyDate = new Date();
        dummyName = "name";
        dummyScreenName = "screen name";
        dummyURL = "url";

        when(mockedStatus.getText()).thenReturn(dummyMessage);
        when(mockedStatus.getCreatedAt()).thenReturn(dummyDate);
        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedUser.getName()).thenReturn(dummyName);
        when(mockedUser.getScreenName()).thenReturn(dummyScreenName);
        when(mockedUser.getProfileImageURL()).thenReturn(dummyURL);

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
    public void testGetTweetsSuccess() throws TwitterException, TwitterServiceResponseException {
        ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(mockedStatus);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        List<Tweet> actualList = service.getHomeTimeline();

        verify(api).getHomeTimeline();
        assertNotNull(actualList);
        assertEquals(dummyList.size(), actualList.size());
        Tweet tweet = actualList.get(0); // Test tweet is correctly constructed
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyURL, tweet.getUser().getProfileImageUrl());

    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetTweetsServerException() throws TwitterException, TwitterServiceResponseException {
        String errorMessage = "some error message";
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetTweetsNetworkException() throws TwitterException, TwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetTweetsBadAuthException() throws TwitterException, TwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetTweetsCouldNotAuthException() throws TwitterException, TwitterServiceResponseException {
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test
    public void testPostTweetSuccess() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Tweet tweet = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyURL, tweet.getUser().getProfileImageUrl());

    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetNull() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet(null);
        } catch (TwitterServiceCallException e) {
            assertEquals(ResponseUtil.getNullTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetBlank() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet("");
        } catch (TwitterServiceCallException e) {
            assertEquals(ResponseUtil.getInvalidTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetTooLong() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CharacterUtil.MAX_TWEET_LENGTH + 1; i++) {
                sb.append("a"); // single character
            }
            service.postTweet(sb.toString());
        } catch (TwitterServiceCallException e) {
            assertEquals(ResponseUtil.getInvalidTweetErrorMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testPostTweetServerException() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String errorMessage = "some error message";
        TwitterException te = mock(TwitterException.class);
        when(api.updateStatus(anyString())).thenThrow(te);
        when(te.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.postTweet("some message");
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

}

// Need some class to implement ResponseList to stub getHomeTimeline()
class ResponseListImpl<T> extends ArrayList<T> implements ResponseList<T> {

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }

}
