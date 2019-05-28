package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TimelineCache {

    final private int cacheSize;

    private List<Tweet> timelineCache;

    // Cached condition: Retrieved home timeline from Twitter at least once, or posted at least TIMELINE_SIZE tweets
    private boolean cached;

    private Map<String, List<Tweet>> filteredCache;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        timelineCache = new LinkedList<>(); // Must be a LinkedList
        filteredCache = new HashMap<>();
        cached = false;
    }

    // Cached condition: Retrieved home timeline from Twitter at least once, or posted at least TIMELINE_SIZE tweets
    public boolean canGetCachedTimeline() {
        return cached;
    }

    public List<Tweet> getCachedTimeline() {
        if (!canGetCachedTimeline()) {
            logger.warn("Home timeline was never retrieved from Twitter.");
        }
        return timelineCache;
    }

    public void cacheTweets(List<Tweet> timeline) {
        this.timelineCache = new LinkedList<>(timeline);
        cached = true;
    }

    public boolean canGetCachedFilteredTimeline(String keyword) {
        return (cached && filteredCache.containsKey(keyword));
    }

    public List<Tweet> getCachedFilteredTimeline(String keyword) {
        if (!canGetCachedFilteredTimeline(keyword)) {
            final NullPointerException e = new NullPointerException();
            logger.error("Null cache retrieved for keyword: " + keyword, e);
            throw e;
        }
        return filteredCache.get(keyword);
    }

    public void cacheFilteredTimeline(String keyword, List<Tweet> timeline) {

        filteredCache.put(keyword, timeline);
    }

    // Push latest Tweets to cache
    public void pushTweet(Tweet tweet) {
        filteredCache.clear(); // Becomes dirty
        ((LinkedList<Tweet>) timelineCache).addFirst(tweet);
        if (timelineCache.size() > cacheSize) {
            ((LinkedList<Tweet>) timelineCache).removeLast();
        }
        if (timelineCache.size() == TwitterService.TIMELINE_SIZE) {
            cached = true;
        }
    }
}
