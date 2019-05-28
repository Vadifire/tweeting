package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;
import twitter4j.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimelineCacheTest {

    // Class under test
    TimelineCache timelineCache;

    // Dummy vars
    LinkedList<Tweet> dummyTweets; // Contains messages 0 to TIMELINE_SIZE

    @Before
    public void setUp() {
        dummyTweets = new LinkedList<>();

        for (int i = 0; i < TwitterService.TIMELINE_SIZE; i++) {
            final Tweet tweet = new Tweet();
            tweet.setMessage(Integer.toString(i));
            dummyTweets.add(tweet);
        }

        timelineCache = new TimelineCache(TwitterService.TIMELINE_SIZE);

        assertFalse(timelineCache.canGetCachedTimeline());
    }

    @Test
    public void testCacheTweets() {
        timelineCache.cacheTweets(dummyTweets);
        final List<Tweet> cachedTweets = timelineCache.getCachedTimeline();

        assertTrue(timelineCache.canGetCachedTimeline());
        final Set<String> tweetSet = dummyTweets.stream()
                .map(tweet -> tweet.getMessage())
                .collect(Collectors.toSet());
        assertTrue(cachedTweets.stream()
                .allMatch(tweet -> tweetSet.contains(tweet.getMessage())));
    }

    @Test
    public void testCacheStatuses() {
        String dummyMsg = "all";
        LinkedList<Status> statuses = new LinkedList<>();
        for (int i = 0; i < TwitterService.TIMELINE_SIZE; i++) {
            final Status mockedStatus = mock(Status.class); // Avoids having to define Status impl
            when(mockedStatus.getText()).thenReturn(dummyMsg);
            statuses.add(mockedStatus);
        }

        timelineCache.cacheStatuses(statuses);
        final List<Tweet> cachedTweets = timelineCache.getCachedTimeline();


        assertTrue(timelineCache.canGetCachedTimeline());
        assertTrue(cachedTweets.stream().allMatch(t -> t.getMessage().equals(dummyMsg)));
    }

    @Test
    public void testCacheByPosting() {
        dummyTweets.stream()
                .limit(TwitterService.TIMELINE_SIZE - 1)
                .forEach(tweet -> {
                    timelineCache.pushTweet(tweet);
                    assertFalse(timelineCache.canGetCachedTimeline());
                });
        timelineCache.pushTweet(dummyTweets.getLast());
        assertTrue(timelineCache.canGetCachedTimeline());
    }

    @Test
    public void testOverflowCache() {
        Tweet extraDummyTweet = new Tweet();
        extraDummyTweet.setMessage(Integer.toString(TwitterService.MAX_TWEET_LENGTH + 1));

        timelineCache.cacheTweets(dummyTweets);
        timelineCache.pushTweet(extraDummyTweet);
        List<Tweet> cachedTweets = timelineCache.getCachedTimeline();

        assertFalse(cachedTweets.contains(dummyTweets.getLast()));
        assertTrue(cachedTweets.contains(extraDummyTweet));
    }

    @Test
    public void testFilteredCache() {
        final String keyword = dummyTweets.get(0).getMessage();
        final List<Tweet> filteredList = new LinkedList<>();
        filteredList.add(dummyTweets.get(0));
        timelineCache.cacheFilteredTimeline(keyword, filteredList);

        final List<Tweet> actualTweets = timelineCache.getCachedFilteredTimeline(keyword);

        assertTrue(timelineCache.canGetFilteredTimeline(keyword));
        assertEquals(keyword, actualTweets.get(0).getMessage());
    }
}
