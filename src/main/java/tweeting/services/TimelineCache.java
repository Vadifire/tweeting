package tweeting.services;

import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TimelineCache {

    private List<Tweet> timelineCache;

    private HashMap<String, List<Tweet>> filteredTimelineCache; // Store filter results

    public TimelineCache() {
        filteredTimelineCache = new HashMap<>();
    }

    public void invalidate() {
        filteredTimelineCache.clear();
        timelineCache = null;
    }

    public void cacheTimeline(List<Tweet> timeline) {
        this.timelineCache = timeline;
    }

    public void cacheFilteredTimeline(String keyword, List<Tweet> tweets) {
        filteredTimelineCache.put(keyword, tweets);
    }

    public Optional<List<Tweet>> getCachedTimeline() {
        return Optional.ofNullable(timelineCache);
    }

    public Optional<List<Tweet>> getCachedFilteredTimeline(String keyword) {
        return Optional.ofNullable(filteredTimelineCache.get(keyword));
    }

}
