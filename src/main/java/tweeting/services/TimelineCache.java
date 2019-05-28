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

    private Map<String, List<Tweet>> filteredCache;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        tweetCache = new LinkedList<>(); // Must be a LinkedList
        statusCache = new LinkedList<>();
        filteredCache = new HashMap<>();
    }

    // As long as timeline is full, consider it fresh (no external Twitter updates).
    public boolean canGetCachedTimeline() {
        return (tweetCache.size() == cacheSize) || (statusCache.size() == cacheSize);
    }

    public List<Tweet> getCachedTimeline() {
        if (statusCache.size() > 0) { // Convert
            tweetCache = statusCache.stream()
                    .map(Twitter4JUtil::constructTweet)
                    .collect(Collectors.toList());
            statusCache.clear();
        }
        if (!canGetCachedTimeline()) {
            if (tweetCache.size() == 0) {
                logger.warn("Empty timeline was retrieved from cache.");
            } else {
                logger.warn("Incomplete timeline was retrieved from cache.");
            }
        }
        return tweetCache;
    }

    public void cacheTweets(List<Tweet> timeline) {
        this.tweetCache = new LinkedList<>(timeline);
    }
    public void cacheStatuses(List<Status> timeline) {this.statusCache = new LinkedList<>(timeline);}

    public boolean canGetFilteredTimeline(String keyword) {
        return (filteredCache.containsKey(keyword));
    }

    public List<Tweet> getCachedFilteredTimeline(String keyword) {
        if (!canGetFilteredTimeline(keyword)) {
            final NullPointerException e = new NullPointerException();
            logger.error("Null cache retrieved for keyword: " + keyword);
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
    }

}
