package tweeting.services;

import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;

import java.util.ArrayList;

// Need some class to implement ResponseList to stub getHomeTimeline()
public class ResponseListImpl<T> extends ArrayList<T> implements ResponseList<T> {

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }

}
