package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;

public class TimelineCache {

    private boolean fresh;
    private int cacheSize;

    private List<Tweet> timeline;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        fresh = false;
        timeline = new LinkedList<>(); // Must be a LinkedList
    }

    // Return empty optional if stale timeline
    public List<Tweet> getTimeline() {
        if (!isFresh()) {
            logger.warn("Stale timeline cacheTimeline was retrieved.");
        }
        return timeline;
    }

    public boolean isFresh() {
        return fresh;
    }

    // 'Don't worry about external updates' - So no need to dirty cache. Just populate it with pushed Tweets.
    public void pushTweet(Tweet tweet) {
        ((LinkedList<Tweet>)timeline).addFirst(tweet);
        if (timeline.size() == cacheSize) {
            fresh = true;
        }
        if (timeline.size() > cacheSize) {
            ((LinkedList<Tweet>)timeline).removeLast();

        }
    }

    public void cacheTimeline(List<Tweet> timeline) {
        this.timeline = new LinkedList<>(timeline);
        fresh = true;
    }

}
