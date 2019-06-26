package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.util.CharacterUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Twitter4JServiceTest {

    // Mocked classes
    private Twitter api;
    private Status mockedStatus;
    private TwitterException twitterException;

    // Dummy vars
    private String dummyName;
    private String dummyScreenName;
    private String dummyUserUrl;
    private String dummyMessage;
    private Date dummyDate;
    private long dummyId;
    private String dummyTweetUrl;
    // Status List must conform to [a, aa, aaa...] pattern where a is some repeated base String.
    private ResponseListImpl<Status> dummyStatusList;
    private String repeated; // Must have length > 0

    // Class under test
    private Twitter4JService service;

    @Before
    public void setUp() {
        mockedStatus = mock(Status.class); // Avoids having to define Status impl
        twitterException = mock(TwitterException.class);
        User mockedUser = mock(User.class); // Avoids having to define User impl
        dummyMessage = "some message";
        dummyDate = new Date();
        dummyName = "name";
        dummyScreenName = "screen name";
        dummyUserUrl = "url";
        dummyId = 1;
        dummyTweetUrl = TwitterService.TWITTER_BASE_URL + dummyScreenName + TwitterService.STATUS_DIRECTORY + dummyId;

        /* Avoids Mock Exceptions */
        when(mockedStatus.getText()).thenReturn(dummyMessage);
        when(mockedStatus.getCreatedAt()).thenReturn(dummyDate);
        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedStatus.getId()).thenReturn(dummyId);
        when(mockedUser.getName()).thenReturn(dummyName);
        when(mockedUser.getScreenName()).thenReturn(dummyScreenName);
        when(mockedUser.get400x400ProfileImageURL()).thenReturn(dummyUserUrl);

        /* For Filtered Timeline Tests */
        dummyStatusList = new ResponseListImpl<>();
        repeated = "a";
        assertTrue(repeated.length() > 0); // Test cases rely on this to be true
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> "a")
                .limit(3) // 3 is used so that filter test cases (filter 0, 1, all) have unique results.
                .forEach(a -> {
                    sb.append(a);
                    Status status = mock(Status.class);
                    when(status.getText()).thenReturn(sb.toString());
                    dummyStatusList.add(status);
                });

        api = mock(Twitter.class);
        service = new Twitter4JService(api);
    }

    /* Utility methods to reduce duplicate code */

    public void assertTweetIsDummy(Tweet tweet) {
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyUserUrl, tweet.getUser().getProfileImageUrl());
        assertEquals(dummyTweetUrl, tweet.getUrl());
    }

    /* End of utility methods */

    @Test
    public void testGetHomeTimelineSuccess() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(mockedStatus);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getHomeTimeline().get();

        verify(api).getHomeTimeline();
        assertNotNull(actualList);
        assertEquals(dummyList.size(), actualList.size());
        final Tweet tweet = actualList.get(0); // Test tweet is correctly constructed
        assertTweetIsDummy(tweet);
    }

    @Test
    public void testGetCachedHomeTimeline() throws TwitterServiceResponseException, TwitterException {
        testGetHomeTimelineSuccess();
        testGetHomeTimelineSuccess();
        verify(api, times(1)).getHomeTimeline();
    }

    @Test
    public void testGetHomeTimelineWithANullTweet() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(null);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getHomeTimeline().get();

        verify(api).getHomeTimeline();
        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetHomeTimelineServerException() throws TwitterException, TwitterServiceResponseException {
        final String errorMessage = "some error message";
        when(api.getHomeTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetHomeTimelineNetworkException() throws TwitterException, TwitterServiceResponseException {
        when(api.getHomeTimeline()).thenThrow(twitterException);
        when(twitterException.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetHomeTimelineBadAuthException() throws TwitterException, TwitterServiceResponseException {
        when(api.getHomeTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetHomeTimelineCouldNotAuthException() throws TwitterException, TwitterServiceResponseException {
        when(api.getHomeTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());
        try {
            service.getHomeTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }


    @Test
    public void testGetUserTimelineSuccess() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(mockedStatus);

        when(api.getUserTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getUserTimeline().get();

        verify(api).getUserTimeline();
        assertNotNull(actualList);
        assertEquals(dummyList.size(), actualList.size());
        final Tweet tweet = actualList.get(0); // Test tweet is correctly constructed
        assertTweetIsDummy(tweet);
    }

    @Test
    public void testGetCachedUserTimeline() throws TwitterServiceResponseException, TwitterException {
        testGetUserTimelineSuccess();
        testGetUserTimelineSuccess();
        verify(api, times(1)).getUserTimeline();
    }

    @Test
    public void testGetUserTimelineWithANullTweet() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(null);

        when(api.getUserTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getUserTimeline().get();

        verify(api).getUserTimeline();
        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetUserTimelineServerException() throws TwitterException, TwitterServiceResponseException {
        final String errorMessage = "some error message";
        when(api.getUserTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getUserTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetUserTimelineNetworkException() throws TwitterException, TwitterServiceResponseException {
        when(api.getUserTimeline()).thenThrow(twitterException);
        when(twitterException.isCausedByNetworkIssue()).thenReturn(true);
        try {
            service.getUserTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetUserTimelineBadAuthException() throws TwitterException, TwitterServiceResponseException {
        when(api.getUserTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());
        try {
            service.getUserTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetUserTimelineCouldNotAuthException() throws TwitterException, TwitterServiceResponseException {
        when(api.getUserTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());
        try {
            service.getUserTimeline();
        } catch (TwitterServiceResponseException e) {
            assertEquals(TwitterService.SERVICE_UNAVAILABLE_MESSAGE, e.getMessage());
            throw e;
        }
    }


    @Test
    public void testPostTweetSuccess() throws TwitterException, TwitterServiceResponseException,
            TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus);

        final Tweet tweet = service.postTweet(dummyMessage).get();

        verify(api).updateStatus(anyString());
        assertTweetIsDummy(tweet);
    }

    @Test
    public void testPostTweetNullUser()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(mockedStatus);
        when(mockedStatus.getUser()).thenReturn(null);

        final Tweet tweet = service.postTweet(dummyMessage).get();

        verify(api).updateStatus(anyString());
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertNull(tweet.getUser());
        assertEquals(dummyDate, tweet.getCreatedAt());
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetNullMessage() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet(null);
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.MISSING_TWEET_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetBlank() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.postTweet("");
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.MISSING_TWEET_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testPostTweetTooLong() throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            StringBuilder sb = new StringBuilder();
            Stream.generate(() -> "a")
                    .limit(CharacterUtil.MAX_TWEET_LENGTH + 1)
                    .forEach(sb::append);
            service.postTweet(sb.toString());
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.TOO_LONG_TWEET_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testPostTweetServerException()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        final String errorMessage = "some error message";
        when(api.updateStatus(anyString())).thenThrow(twitterException);
        when(twitterException.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.postTweet("some message");
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test
    public void testFilterAllResults()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        final List<Tweet> tweetList = service.getFilteredTimeline(repeated).get();

        assertTrue(dummyStatusList.stream() // Extra validation that all statuses contain repeated base String
                .allMatch(status -> status.getText().contains(repeated)));
        assertEquals(dummyStatusList.size(), tweetList.size());
        final Set<String> statusSet = dummyStatusList.stream()
                .map(Status::getText)
                .collect(Collectors.toSet());
        assertTrue(tweetList.stream()
                .allMatch(tweet -> statusSet.contains(tweet.getMessage())));
    }

    @Test
    public void testFilterOneResult()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        final String dummyKeyword = dummyStatusList.get(dummyStatusList.size() - 1).getText();
        // The dummy keyword should guarantee excluding all but the last tweet
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        final List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();
        assertEquals(1, tweetList.size());
        assertEquals(dummyStatusList.get(dummyStatusList.size() - 1).getText(), tweetList.get(0).getMessage());
    }

    @Test
    public void testFilterNoResults()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        final String dummyKeyword = dummyStatusList.get(dummyStatusList.size() - 1).getText() + repeated; // filters all
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        final List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();
        assertEquals(0, tweetList.size());
    }

    @Test(expected = TwitterServiceCallException.class)
    public void testFilterMissingKeyword()
            throws TwitterServiceResponseException, TwitterServiceCallException {
        try {
            service.getFilteredTimeline(null);
        } catch (TwitterServiceCallException e) {
            assertEquals(TwitterService.MISSING_KEYWORD_MESSAGE, e.getMessage());
            throw e;
        }
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testFilterServerException()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        final String errorMessage = "some error message";
        when(api.getHomeTimeline()).thenThrow(twitterException);
        when(twitterException.getErrorMessage()).thenReturn(errorMessage);
        try {
            service.getFilteredTimeline("some keyword");
        } catch (TwitterServiceResponseException e) {
            assertEquals(errorMessage, e.getMessage());
            throw e;
        }
    }

    @Test
    public void testFilterAllResultsExceptFirstNullMessage()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        final String dummyKeyword = dummyStatusList.get(0).getText();
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);
        when(dummyStatusList.get(0).getText()).thenReturn(null); // Make a Status have null message

        final List<Tweet> tweetList = service.getFilteredTimeline(dummyKeyword).get();

        verify(api).getHomeTimeline();
        assertEquals(dummyStatusList.size() - 1, tweetList.size());
        Set<String> statusSet = dummyStatusList.stream()
                .filter(tweet -> tweet.getText() != null) // Ignore any null messages
                .map(Status::getText)
                .collect(Collectors.toSet());
        assertTrue(tweetList.stream()
                .allMatch(tweet -> statusSet.contains(tweet.getMessage())));
    }


    @Test
    public void testMissFilterCacheHitTimelineCache()
            throws TwitterServiceResponseException, TwitterException, TwitterServiceCallException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(mockedStatus);
        when(api.getHomeTimeline()).thenReturn(dummyList);

        service.getHomeTimeline();
        final List<Tweet> tweetList = service.getFilteredTimeline(mockedStatus.getText()).get();
        assertEquals(1, tweetList.size());
        assertEquals(mockedStatus.getText(), tweetList.get(0).getMessage());

        verify(api, times(1)).getHomeTimeline();
    }

    @Test
    public void testFilterCacheHit()
            throws TwitterServiceCallException, TwitterServiceResponseException, TwitterException {
        testFilterAllResults();
        testFilterAllResults();
        verify(api, times(1)).getHomeTimeline();
    }

}