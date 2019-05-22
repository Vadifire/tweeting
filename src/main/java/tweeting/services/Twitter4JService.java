package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.models.TwitterUser;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class Twitter4JService implements TwitterService {

    private Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    private TimelineCache timelineCache;

    @Inject
    public Twitter4JService(Twitter api) {
        this.api = api;
        this.timelineCache = new TimelineCache(); //TODO: dep inject
    }

    @Override
    public Optional<Tweet> postTweet(String message) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (StringUtils.isBlank(message)) {
            throw new TwitterServiceCallException(MISSING_TWEET_MESSAGE);
        }
        if (message.length() > MAX_TWEET_LENGTH) {
            throw new TwitterServiceCallException(TOO_LONG_TWEET_MESSAGE);
        }
        try {
            return Optional.ofNullable(api.updateStatus(message))
                    .map(status -> {
                        logger.info("Successfully posted '{}' to Twitter.", message);
                        final Tweet tweet = constructTweet(status);
                        timelineCache.pushTweet(tweet); // Update cache
                        return tweet;
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        try {

            Optional<List<Tweet>> cachedTweets = timelineCache.getTimeline();

            if (cachedTweets.isPresent()) { //TODO make this observe better optional syntax
                logger.info("Successfully retrieved home timeline from cache.");
                return cachedTweets;
            }
           return Optional.ofNullable(api.getHomeTimeline())
                    .map(statuses -> {
                        logger.info("Successfully retrieved home timeline from Twitter.");
                        List<Tweet> tweets = constructTweetList(statuses);
                        timelineCache.cache(tweets);
                        return tweets;
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getFilteredTimeline(String keyword) throws TwitterServiceResponseException,
            TwitterServiceCallException {
        if (StringUtils.isBlank(keyword)) {
            throw new TwitterServiceCallException(MISSING_KEYWORD_MESSAGE);
        }
        return getHomeTimeline().map(tweets -> tweets.stream()
                .filter(t -> StringUtils.containsIgnoreCase(t.getMessage(), keyword))
                .collect(Collectors.toList())
        );
    }

    private TwitterServiceResponseException createServerException(TwitterException te) {
        if (te.isCausedByNetworkIssue() || te.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                te.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            return new TwitterServiceResponseException(SERVICE_UNAVAILABLE_MESSAGE, te);
        } else {
            return new TwitterServiceResponseException(te);
        }
    }

    private Tweet constructTweet(Status status) {
        if (status == null) {
            return null;
        }
        Tweet tweet = new Tweet();
        tweet.setMessage(status.getText());
        if (status.getUser() == null) {
            logger.warn("Tweet has no user.");
        } else {
            TwitterUser user = new TwitterUser();
            user.setTwitterHandle(status.getUser().getScreenName());
            user.setName(status.getUser().getName());
            user.setProfileImageUrl(status.getUser().getProfileImageURL());
            tweet.setUser(user);
        }
        tweet.setCreatedAt(status.getCreatedAt());
        return tweet;
    }

    private List<Tweet> constructTweetList(List<Status> statuses) {
        return statuses.stream()
                .map(this::constructTweet)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}