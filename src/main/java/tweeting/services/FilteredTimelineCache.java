package tweeting.services;

import tweeting.models.Tweet;

import java.util.HashMap;
import java.util.List;

public class FilteredTimelineCache {
    private HashMap<String, List<Tweet>> filteredTimelineCache; // Store filter results

    public FilteredTimelineCache() {
        filteredTimelineCache = new HashMap<>();
    }

    public void invalidate() {
        filteredTimelineCache.clear();
    }

    public void cacheTweets(String keyword, List<Tweet> tweets) {
        filteredTimelineCache.put(keyword, tweets);
    }

    public List<Tweet> getTweets(String keyword) {
        return filteredTimelineCache.get(keyword);
    }

    public boolean containsKeyword(String keyword) {
        return filteredTimelineCache.containsKey(keyword);
    }
}
