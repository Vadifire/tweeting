package main.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.List;

public class TwitterAPIWrapperImpl implements TwitterAPIWrapper {

    public List<Status> getHomeTimeline() throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        return twitter.getHomeTimeline(); //Retrieve Statuses using Twitter4J
    }

    public Status updateStatus(String message) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        return twitter.updateStatus(message); // Post Status update using Twitter4J API
    }

}
