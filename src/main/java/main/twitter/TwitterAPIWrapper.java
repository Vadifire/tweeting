package main.twitter;

/*
 * The purpose of this interface is to separate Twitter API calls (through Twitter4J) from this server's own API
 *
 * Mocking this interface helps make our API more unit testable
 */

import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public interface TwitterAPIWrapper {

    List<Status> getHomeTimeline() throws TwitterException; // Retrieve Statuses from Home Timeline using Twitter4J

    int MAX_TWEET_LENGTH = 280;

    Status updateStatus(String message) throws TwitterException;  // Post Status update using Twitter4J API

}
