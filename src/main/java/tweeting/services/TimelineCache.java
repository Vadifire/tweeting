package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import twitter4j.Status;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimelineCache {

    final private int cacheSize;

    private List<Status> statusCache; // Why? Only perform Status->Tweet conversion when necessary
    private List<Tweet> tweetCache;

    private boolean fresh;

    private Map<String, List<Tweet>> filteredCache;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        tweetCache = new LinkedList<>(); // Must be a LinkedList
        statusCache = new LinkedList<>();
        filteredCache = new HashMap<>();
        fresh = false; // Retrieved Tweets from Twitter at least once
    }

    // Fresh condition: Retrieved home timeline from Twitter at least once, or posted at least TIMELINE_SIZE tweets
    public boolean isTimelineFresh() {
        return fresh;
    }

    public List<Tweet> getCachedTimeline() {
        if (!isTimelineFresh()) {
            logger.warn("Home timeline was never retrieved from Twitter.");
        }
        if (statusCache.size() > 0) { // Convert
            final List<Tweet> convertedTweets = statusCache.stream()
                    .limit(TwitterService.TIMELINE_SIZE - tweetCache.size())
                    .map(Twitter4JUtil::constructTweet)
                    .collect(Collectors.toList());
            tweetCache.addAll(convertedTweets);
            statusCache.clear();
        }
        return tweetCache;
    }

    public void cacheTweets(List<Tweet> timeline) {
        this.tweetCache = new LinkedList<>(timeline);
        fresh = true;
    }
    public void cacheStatuses(List<Status> timeline) {
        this.statusCache = new LinkedList<>(timeline);
        fresh = true;
    }

    public boolean filterCacheContainsKeyword(String keyword) {
        return (filteredCache.containsKey(keyword));
    }

    public List<Tweet> getCachedFilteredTimeline(String keyword) {
        if (!filterCacheContainsKeyword(keyword)) {
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
        ((LinkedList<Tweet>)tweetCache).addFirst(tweet);
        if (tweetCache.size() > cacheSize) {
            ((LinkedList<Tweet>)tweetCache).removeLast();
        }
        if (tweetCache.size() == TwitterService.TIMELINE_SIZE) {
            fresh = true;
        }
    }

}
