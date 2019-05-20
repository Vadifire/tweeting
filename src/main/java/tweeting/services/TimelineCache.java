package tweeting.services;

import tweeting.models.Tweet;

import java.util.LinkedList;
import java.util.List;

public class TimelineCache {

    public static int TIMELINE_SIZE = 20;

    List<Tweet> timeline;

    public TimelineCache() {
        timeline = new LinkedList<>();
    }

    public List<Tweet> getTimeline() {
        return timeline;
    }

    public void pushTweet(Tweet tweet) {
        ((LinkedList) timeline).addFirst(tweet);
        if (timeline.size() > 20)
            ((LinkedList) timeline).removeLast();
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = timeline;
    }

    public boolean isValid() {
        return timeline.size() == TIMELINE_SIZE;
    }

}
