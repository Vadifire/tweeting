package tweeting.services;

import tweeting.models.Tweet;

import java.util.List;

public class TimelineCache {

    private List<Tweet> timelineCache;

    public List<Tweet> getCachedTimeline() {
        return timelineCache;
    }

    public void invalidate() {
        timelineCache = null;
    }

    public void cacheTweets(List<Tweet> timeline) {
        this.timelineCache = timeline;
    }
}
