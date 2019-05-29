package tweeting.services;

import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.List;

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

    public List<Tweet> getCachedTimeline() {
        return timelineCache;
    }

    public boolean containsKeyword(String keyword) {
        return filteredTimelineCache.containsKey(keyword);
    }

    public List<Tweet> getCachedFilteredTimeline(String keyword) {
        return filteredTimelineCache.get(keyword);
    }

}
