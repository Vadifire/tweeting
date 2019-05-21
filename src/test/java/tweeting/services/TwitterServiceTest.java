package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.conf.TwitterOAuthCredentials;
import tweeting.models.Tweet;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.util.CharacterUtil;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    String repeated;
    ResponseListImpl<Status> dummyStatusList;

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

        dummyStatusList = new ResponseListImpl<>();
        /* For Filtered Timeline Tests */
        repeated = "a";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            sb.append("a");
            Status status = mock(Status.class);
            when(status.getText()).thenReturn(sb.toString());
            dummyStatusList.add(status);
        }
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

        List<Tweet> actualList = service.getHomeTimeline().get();

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

    @Test
    public void testGetTweetsNullTimeline() throws TwitterException, TwitterServiceResponseException {
        when(api.getHomeTimeline()).thenReturn(null);

        Optional<List<Tweet>> tweets = service.getHomeTimeline();

        verify(api).getHomeTimeline();
        assertEquals(Optional.empty(), tweets);
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
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
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
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
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
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test
    public void testPostTweetSuccess() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        Tweet tweet = service.postTweet(dummyMessage).get();

        verify(api).updateStatus(anyString());
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyURL, tweet.getUser().getProfileImageUrl());
    }

    @Test
    public void testPostNullTweet() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(null);

        Optional<Tweet> tweet = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertEquals(Optional.empty(), tweet);
    }

    @Test
    public void testPostTweetNullUser() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus);
        when(mockedStatus.getUser()).thenReturn(null);

        Tweet tweet = service.postTweet(dummyMessage).get();

        verify(api).updateStatus(anyString());
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(null, tweet.getUser());
        assertEquals(dummyDate, tweet.getCreatedAt());
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetNullMessage() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet(null);
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.NULL_TWEET_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetBlank() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet("");
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.INVALID_TWEET_MESSAGE, e.getMessage());
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
            assertEquals(TwitterService.INVALID_TWEET_MESSAGE, e.getMessage());
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

    @Test
    public void testFilterAllResults() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyKeyword = dummyStatusList.get(0).getText();
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();

        assertEquals(dummyStatusList.size(), tweetList.size());
        for (int i = 0; i < dummyStatusList.size(); i++) {
            assertEquals(dummyStatusList.get(i).getText(), tweetList.get(i).getMessage());
        }
    }

    @Test
    public void testFilterOneResult() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyKeyword = dummyStatusList.get(dummyStatusList.size() - 1).getText();

        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();

        assertEquals(1, tweetList.size());
        assertEquals(dummyStatusList.get(dummyStatusList.size() - 1).getText(), tweetList.get(0).getMessage());
    }

    @Test
    public void testFilterNoResults() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyKeyword = dummyStatusList.get(dummyStatusList.size() - 1).getText() + repeated;

        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();

        assertEquals(0, tweetList.size());
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testFilterMissingKeyword() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.getFilteredTimeline(null);
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.NULL_KEYWORD_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testFilterServerException() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String errorMessage = "some error message";
        TwitterException te = mock(TwitterException.class);
        when(api.getHomeTimeline()).thenThrow(te);
        when(te.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getFilteredTimeline("some keyword");
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test
    public void testFilterNullTimeline() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.getHomeTimeline()).thenReturn(null);

        Optional<List<Tweet>> tweets = service.getFilteredTimeline("some keyword");

        verify(api).getHomeTimeline();
        assertEquals(Optional.empty(), tweets);
    }

    @Test
    public void testFilterAllResultsExceptFirstNullMessage() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        String dummyKeyword = dummyStatusList.get(0).getText();
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);
        when(dummyStatusList.get(0).getText()).thenReturn(null); // Uh oh!

        List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();

        assertEquals(dummyStatusList.size() - 1, tweetList.size());
        for (int i = 0; i < dummyStatusList.size() - 1; i++) {
            assertEquals(dummyStatusList.get(i + 1).getText(), tweetList.get(i).getMessage());
        }
    }

}