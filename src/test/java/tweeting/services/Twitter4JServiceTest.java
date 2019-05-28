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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Twitter4JServiceTest {

    // Mocked classes
    private Twitter api;
    private TimelineCache cache;
    private Status mockedStatus;

    // Dummy vars
    private String dummyName;
    private String dummyScreenName;
    private String dummyURL;
    private String dummyMessage;
    private Date dummyDate;
    // Status List must conform to [a, aa, aaa...] pattern where a is some repeated base String.
    private ResponseListImpl<Status> dummyStatusList;
    private String repeated; // Must have length > 0

    // Class under test
    private Twitter4JService service;

    @Before
    public void setUp() {
        mockedStatus = mock(Status.class); // Avoids having to define Status impl
        User mockedUser = mock(User.class); // Avoids having to define User impl
        dummyMessage = "some message";
        dummyDate = new Date();
        dummyName = "name";
        dummyScreenName = "screen name";
        dummyURL = "url";

        /* Avoids Mock Exceptions */
        when(mockedStatus.getText()).thenReturn(dummyMessage);
        when(mockedStatus.getCreatedAt()).thenReturn(dummyDate);
        when(mockedStatus.getUser()).thenReturn(mockedUser);
        when(mockedUser.getName()).thenReturn(dummyName);
        when(mockedUser.getScreenName()).thenReturn(dummyScreenName);
        when(mockedUser.getProfileImageURL()).thenReturn(dummyURL);

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
        cache = mock(TimelineCache.class);
        service = new Twitter4JService(api, cache); // Fine for single-class unit tests (https://dagger.dev/testing.html)
    }

    @Test
    public void testGetTweetsSuccess() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(mockedStatus);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getHomeTimeline().get();

        verify(api).getHomeTimeline();
        assertNotNull(actualList);
        assertEquals(dummyList.size(), actualList.size());
        final Tweet tweet = actualList.get(0); // Test tweet is correctly constructed
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyURL, tweet.getUser().getProfileImageUrl());
    }

    @Test
    public void testGetCachedTweets() throws TwitterServiceResponseException, TwitterException {
        final List<Tweet> cachedTweets = new LinkedList<>();
        when(cache.isFresh()).thenReturn(true);
        when(cache.getCachedTimeline()).thenReturn(cachedTweets);

        final List<Tweet> actualList = service.getHomeTimeline().get();

        verify(api, never()).getHomeTimeline();
        assertEquals(cachedTweets, actualList);
    }

    @Test
    public void testGetTweetsWithANullTweet() throws TwitterException, TwitterServiceResponseException {
        final ResponseListImpl<Status> dummyList = new ResponseListImpl<>();
        dummyList.add(null);

        when(api.getHomeTimeline()).thenReturn(dummyList);

        final List<Tweet> actualList = service.getHomeTimeline().get();

        verify(api).getHomeTimeline();
        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @Test
    public void testGetTweetsNullTimeline() throws TwitterException, TwitterServiceResponseException {
        when(api.getHomeTimeline()).thenReturn(null);

        final Optional<List<Tweet>> tweets = service.getHomeTimeline();

        verify(api).getHomeTimeline();
        assertEquals(Optional.empty(), tweets);
    }

    @Test(expected = TwitterServiceResponseException.class)
    public void testGetTweetsServerException() throws TwitterException, TwitterServiceResponseException {
        final String errorMessage = "some error message";
        final TwitterException te = mock(TwitterException.class);
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
        final TwitterException te = mock(TwitterException.class);
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
        final TwitterException te = mock(TwitterException.class);
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
        final TwitterException te = mock(TwitterException.class);
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

        final Tweet tweet = service.postTweet(dummyMessage).get();

        verify(api).updateStatus(anyString());
        assertNotNull(tweet);
        assertEquals(dummyMessage, tweet.getMessage());
        assertEquals(dummyName, tweet.getUser().getName());
        assertEquals(dummyScreenName, tweet.getUser().getTwitterHandle());
        assertEquals(dummyDate, tweet.getCreatedAt());
        assertEquals(dummyURL, tweet.getUser().getProfileImageUrl());
    }

    @Test
    public void testPostNullTweet()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        when(api.updateStatus(anyString())).thenReturn(null);

        final Optional<Tweet> tweet = service.postTweet(dummyMessage);

        verify(api).updateStatus(anyString());
        assertEquals(Optional.empty(), tweet);
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
        assertEquals(null, tweet.getUser());
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
        final TwitterException te = mock(TwitterException.class);
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
    public void testFilterAllResults()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        when(api.getHomeTimeline()).thenReturn(dummyStatusList);

        final List<Tweet> tweetList = service.getFilteredTimeline(repeated).get();

        assertTrue(dummyStatusList.stream() // Extra validation that all statuses contain repeated base String
                .allMatch(status -> status.getText().contains(repeated)));
        verify(api).getHomeTimeline();
        assertEquals(dummyStatusList.size(), tweetList.size());
        final Set<String> statusSet = dummyStatusList.stream()
                .map(status -> status.getText())
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
        final TwitterException te = mock(TwitterException.class);
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
    public void testFilterNullTimeline()
            throws TwitterException, TwitterServiceResponseException, TwitterServiceCallException {
        when(api.getHomeTimeline()).thenReturn(null);

        final Optional<List<Tweet>> tweets = service.getFilteredTimeline("some keyword");

        verify(api).getHomeTimeline();
        assertEquals(Optional.empty(), tweets);
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
                .map(status -> status.getText())
                .collect(Collectors.toSet());
        assertTrue(tweetList.stream()
                .allMatch(tweet -> statusSet.contains(tweet.getMessage())));
    }

    @Test
    public void testFilterCacheHit()
            throws TwitterServiceCallException, TwitterServiceResponseException, TwitterException {
        final String dummyKeyword = "keyword";
        final List<Tweet> dummyTweetList = new LinkedList<>();
        final Tweet dummyTweet = new Tweet();
        dummyTweetList.add(dummyTweet);
        when(cache.canGetFilteredTimeline(dummyKeyword)).thenReturn(true);
        when(cache.getCachedFilteredTimeline(dummyKeyword)).thenReturn(dummyTweetList);

        Optional<List<Tweet>> actualList = service.getFilteredTimeline(dummyKeyword);

        verify(api, never()).getHomeTimeline();
        assertEquals(dummyTweetList.size(), actualList.get().size());
        assertEquals(actualList.get().get(0), dummyTweet);
    }

    @Test
    public void testMissFilterCacheHitTimelineCache()
            throws TwitterServiceResponseException, TwitterException, TwitterServiceCallException {
        final String dummyKeyword = "keyword";
        final List<Tweet> cachedTweets = new LinkedList<>();
        final Tweet dummyTweet = new Tweet();
        dummyTweet.setMessage(dummyKeyword);
        cachedTweets.add(dummyTweet);
        when(cache.isFresh()).thenReturn(true);
        when(cache.getCachedTimeline()).thenReturn(cachedTweets);

        final Optional<List<Tweet>> actualList = service.getFilteredTimeline(dummyKeyword);

        verify(api, never()).getHomeTimeline();
        assertEquals(cachedTweets.size(), actualList.get().size());
        assertEquals(dummyTweet, actualList.get().get(0));
    }

}