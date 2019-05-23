package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

    private static final Logger logger = LoggerFactory.getLogger(TimelineCache.class);

    private boolean fresh;
    private int cacheSize;

    private List<Tweet> timeline;

    public TimelineCache(int cacheSize) {
        this.cacheSize = cacheSize;
        fresh = false;
        timeline = new LinkedList<>(); // Must be a LinkedList
    }

    // Return empty optional if stale timeline
    public Optional<List<Tweet>> getTimeline() {
        return Optional.of(timeline)
                .filter(timeline -> fresh);
    }

    // 'Don't worry about external updates' - So tweeting is only thing that can dirty cache
    public void pushTweet(Tweet tweet) {
        ((LinkedList)timeline).addFirst(tweet);
        if (timeline.size() == cacheSize) {
            fresh = true;
        }
        if (timeline.size() > cacheSize) {
            ((LinkedList)timeline).removeLast();

        }
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = new LinkedList(timeline);
        fresh = true;
    }

}
