package tweeting.services;

import tweeting.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TimelineCache {

    List<Tweet> timeline;

    public TimelineCache() {
        timeline = new ArrayList<>();
    }

    public List<Tweet> getTimeline(){
        return timeline;
    }

    public void cacheTimeline(List<Tweet> timeline) {
        this.timeline = timeline;
    }

    public boolean canUse() {
        return false;
    }

}
