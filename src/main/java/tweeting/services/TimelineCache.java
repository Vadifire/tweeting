package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;

public class TimelineCache {

    final private int cacheSize;

    private List<Tweet> timeline;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        timeline = new LinkedList<>(); // Must be a LinkedList
    }

    public List<Tweet> getTimeline() {
        if (!isFresh()) {
            if (timeline.size() == 0) {
                logger.warn("Empty timeline was retrieved from cache.");
            } else {
                logger.warn("Incomplete timeline was retrieved from cache.");
            }
        }
        return timeline;
    }

    // As long as timeline is full, consider it fresh (no external Twitter updates).
    public boolean isFresh() {
        return timeline.size() == cacheSize;
    }

    // Push latest Tweets to cache
    public void pushTweet(Tweet tweet) {
        ((LinkedList<Tweet>)timeline).addFirst(tweet);
        if (timeline.size() > cacheSize) {
            ((LinkedList<Tweet>)timeline).removeLast();
        }
    }

    public void cacheTimeline(List<Tweet> timeline) {
        this.timeline = new LinkedList<>(timeline);
    }

}
