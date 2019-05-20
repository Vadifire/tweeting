package tweeting.services;

import tweeting.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TimelineCache {

    List<Tweet> timeline;
    boolean valid;

    public TimelineCache() {
        this.valid = false;
        timeline = new ArrayList<>();
    }

    public List<Tweet> getTimeline(){
        return timeline;
    }

    public void cache(List<Tweet> timeline) {
        this.timeline = timeline;
        this.valid = true;
    }

    public void invalidate() {
        this.valid = false;
    }

    public boolean canUse() {
        return valid;
    }

}
