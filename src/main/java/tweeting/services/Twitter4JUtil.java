package tweeting.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.models.TwitterUser;
import twitter4j.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Twitter4JUtil {

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JUtil.class);

    public static Tweet constructTweet(Status status) {
        if (status == null) {
            return null;
        }
        final Tweet tweet = new Tweet();
        tweet.setMessage(status.getText());
        if (status.getUser() == null) {
            logger.warn("Tweet has no user.");
        } else {
            final TwitterUser user = new TwitterUser();
            user.setTwitterHandle(status.getUser().getScreenName());
            user.setName(status.getUser().getName());
            user.setProfileImageUrl(status.getUser().getProfileImageURL());
            tweet.setUser(user);
        }
        tweet.setCreatedAt(status.getCreatedAt());
        return tweet;
    }

    public static List<Tweet> constructTweetList(List<Status> statuses) {
        return statuses
                .stream()
                .map(Twitter4JUtil::constructTweet)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
