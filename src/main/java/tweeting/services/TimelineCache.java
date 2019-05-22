package tweeting.services;

import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

    public static int TIMELINE_SIZE = 20; // TODO: consider where this belongs

    List<Tweet> timeline;

    public TimelineCache() {
        timeline = new LinkedList<>();
    }

    // Optional because want to force Service to retrieve timeline again if not cached
    public Optional<List<Tweet>> getTimeline() {
        return Optional.of(timeline)
                .filter(t -> t.size() == TIMELINE_SIZE);
    }

    // 'Don't worry about external updates' - So tweeting is only thing that can dirty cache
    public void pushTweet(Tweet tweet) {
        ((LinkedList) timeline).addFirst(tweet);
        if (timeline.size() > TIMELINE_SIZE)
            ((LinkedList) timeline).removeLast();
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = timeline;
    }

}
