package tweeting.services;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.Twitter;

import java.util.List;

public class TwitterService {

    private static TwitterService instance;

    private Twitter api;

    private TwitterService() {
    }

    // 'Double Checked Locking' to make getInstance() thread safe
    public static TwitterService getInstance() {
        if (instance == null) {
            synchronized (TwitterService.class) {
                if (instance == null) {
                    instance = new TwitterService();
                }
            }
        }
        return instance;
    }

    public void setAPI(Twitter api) {
        this.api = api;
    }

    public List<Status> getTweets() throws TwitterException {
        return api.getHomeTimeline();
    }

    public Status postTweet(String message) throws TwitterException {
        return api.updateStatus(message);
    }

}
