package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TimelineCacheTest {

    // Class under test
    TimelineCache timelineCache;

    LinkedList<Tweet> dummyTweets;

    @Before
    public void setUp() {
        dummyTweets = new LinkedList<>();

        for (int i = 0; i < TwitterService.TIMELINE_SIZE; i++) {
            final Tweet tweet = new Tweet();
            tweet.setMessage(Integer.toString(i));
            dummyTweets.add(tweet);
        }

        timelineCache = new TimelineCache(TwitterService.TIMELINE_SIZE);

        assertFalse(timelineCache.isFresh());
    }

    @Test
    public void testCacheTimeline() {
        timelineCache.cacheTimeline(dummyTweets);

        List<Tweet> cachedTweets = timelineCache.getTimeline();

        assertTrue(timelineCache.isFresh());

        Set<String> tweetSet = dummyTweets.stream()
                .map(tweet -> tweet.getMessage())
                .collect(Collectors.toSet());

        assertTrue(cachedTweets.stream()
                .allMatch(tweet -> tweetSet.contains(tweet.getMessage())));
    }

    @Test
    public void testCacheByPosting() {
        dummyTweets.stream()
                .limit(TwitterService.TIMELINE_SIZE - 1)
                .forEach(tweet -> {
                    timelineCache.pushTweet(tweet);
                    assertFalse(timelineCache.isFresh());
                });
        timelineCache.pushTweet(dummyTweets.getLast());
        assertTrue(timelineCache.isFresh());
    }

    @Test
    public void testOverflowCache() {
        Tweet extraDummyTweet = new Tweet();
        extraDummyTweet.setMessage(Integer.toString(TwitterService.MAX_TWEET_LENGTH + 1));

        timelineCache.cacheTimeline(dummyTweets);
        timelineCache.pushTweet(extraDummyTweet);
        List<Tweet> cachedTweets = timelineCache.getTimeline();

        assertFalse(cachedTweets.contains(dummyTweets.getLast()));
        assertTrue(cachedTweets.contains(extraDummyTweet));
    }
}
