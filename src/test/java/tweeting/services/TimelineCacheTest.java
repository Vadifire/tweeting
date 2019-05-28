package tweeting.services;

import org.junit.Before;
import org.junit.Test;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class TimelineCacheTest {

    // Class under test
    private TimelineCache timelineCache;

    // Dummy vars
    private LinkedList<Tweet> dummyTweets; // Contains messages 0 to TIMELINE_SIZE

    @Before
    public void setUp() {
        dummyTweets = new LinkedList<>();

        for (int i = 0; i < TwitterService.TIMELINE_SIZE; i++) {
            final Tweet tweet = new Tweet();
            tweet.setMessage(Integer.toString(i));
            dummyTweets.add(tweet);
        }

        timelineCache = new TimelineCache();

    }

    @Test
    public void testCacheTweets() {
        timelineCache.cacheTweets(dummyTweets);
        final List<Tweet> cachedTweets = timelineCache.getCachedTimeline();

        final Set<String> tweetSet = dummyTweets.stream()
                .map(Tweet::getMessage)
                .collect(Collectors.toSet());
        assertTrue(cachedTweets.stream()
                .allMatch(tweet -> tweetSet.contains(tweet.getMessage())));
    }

    @Test
    public void testInvalidate() {
        timelineCache.cacheTweets(dummyTweets);
        timelineCache.invalidate();
        assertNull(timelineCache.getCachedTimeline());
    }

}
