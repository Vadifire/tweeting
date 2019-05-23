package tweeting.services;

import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

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
        ((LinkedList<Tweet>)timeline).addFirst(tweet);
        if (timeline.size() == cacheSize) {
            fresh = true;
        }
        if (timeline.size() > cacheSize) {
            ((LinkedList<Tweet>)timeline).removeLast();

        }
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = new LinkedList<>(timeline);
        fresh = true;
    }

}
