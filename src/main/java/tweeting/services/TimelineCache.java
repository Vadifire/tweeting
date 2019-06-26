package tweeting.services;

import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

    private HashMap<String, List<Tweet>> timelineCache;

    public TimelineCache() {
        timelineCache = new HashMap<>();
    }

    public void invalidate() {
        timelineCache.clear();
    }

    public void cacheTimeline(String key, List<Tweet> tweets) {
        timelineCache.put(key, tweets);
    }

    public Optional<List<Tweet>> getCachedTimeline(String key) {
        return Optional.ofNullable(timelineCache.get(key));
    }

}
