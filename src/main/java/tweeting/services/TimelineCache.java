package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;

public class TimelineCache {

    private static final Logger logger = LoggerFactory.getLogger(TimelineCache.class);

    private boolean fresh;
    private int cacheSize;

    private LinkedList<Tweet> timeline;

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        fresh = false;
        timeline = new LinkedList<>();
    }

    public LinkedList<Tweet> getTimeline() {
        if (!isFresh()) {
            logger.warn("Retrieved a dirty cache.");
        }
        return timeline;
    }

    public boolean isFresh() { // TODO: how can this be better designed for users?
        return fresh;
    }

    // 'Don't worry about external updates' - So tweeting is only thing that can dirty cache
    public void pushTweet(Tweet tweet) {
        timeline.addFirst(tweet);
        if (timeline.size() == cacheSize) {
            fresh = true;
        }
        if (timeline.size() > cacheSize) {
            timeline.removeLast();

        }
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = new LinkedList(timeline);
        fresh = true;
    }

}
