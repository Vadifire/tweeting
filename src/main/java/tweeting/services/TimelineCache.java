package tweeting.services;

import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

    private HashMap<String, List<Tweet>> filteredTimelineCache; // Store filter results

    public TimelineCache() {
        filteredTimelineCache = new HashMap<>();
    }

    public void invalidate() {
        filteredTimelineCache.clear();
    }

    public void cacheTimeline(String keyword, List<Tweet> tweets) {
        filteredTimelineCache.put(keyword, tweets);
    }

    public Optional<List<Tweet>> getCachedTimeline(String keyword) {
        return Optional.ofNullable(filteredTimelineCache.get(keyword));
    }

}
