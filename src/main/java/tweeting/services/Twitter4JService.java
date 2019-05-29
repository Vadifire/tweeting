package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import tweeting.models.TwitterUser;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class Twitter4JService implements TwitterService {

    private final Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    private TimelineCache homeTimelineCache;

    public Twitter4JService(Twitter api) {
        this.api = api;
        homeTimelineCache = new TimelineCache();
    }

    @Override
    public Tweet postTweet(String message)
            throws TwitterServiceResponseException, TwitterServiceCallException {
        if (StringUtils.isBlank(message)) {
            throw new TwitterServiceCallException(MISSING_TWEET_MESSAGE);
        }
        else if (message.length() > MAX_TWEET_LENGTH) {
            throw new TwitterServiceCallException(TOO_LONG_TWEET_MESSAGE);
        }
        try {
            return Optional.ofNullable(api.updateStatus(message))
                    .map(status -> {
                        logger.info("Successfully posted '{}' to Twitter.", message);
                        final Tweet tweet = constructTweet(status);
                        homeTimelineCache.invalidate();
                        return tweet;
                    }).get();
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }


    @Override
    public List<Tweet> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            final Optional<List<Tweet>> cachedTweets = homeTimelineCache.getCachedTimeline("");
            if (cachedTweets.isPresent()) {
                logger.info("Successfully retrieved home timeline from cache.");
                return cachedTweets.get();
            }
            final List<Status> statuses = api.getHomeTimeline();
            final List<Tweet> tweets = statuses.stream()
                    .map(this::constructTweet)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            homeTimelineCache.cacheTimeline("", tweets);
            logger.info("Successfully retrieved home timeline from Twitter.");
            return tweets;
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    //logger.info("Successfully retrieved home timeline filtered by \'" + keyword + "\' from cache.");
    @Override
    public List<Tweet> getFilteredTimeline(String keyword)
            throws TwitterServiceResponseException, TwitterServiceCallException {
        if (StringUtils.isBlank(keyword)) {
            throw new TwitterServiceCallException(MISSING_KEYWORD_MESSAGE);
        }
        // Try to pull filtered result from cache.
        final Optional<List<Tweet>> cachedFilteredTweets = homeTimelineCache.getCachedTimeline(keyword);
        if (cachedFilteredTweets.isPresent()) {
            homeTimelineCache.cacheTimeline(keyword, cachedFilteredTweets.get());
            logger.info("Successfully retrieved home timeline from cache.");
            return cachedFilteredTweets.get();
        } // Otherwise, make call to Twitter
        final List<Tweet> filteredTweets = getHomeTimeline()
                    .stream()
                    .filter(tweet -> StringUtils.containsIgnoreCase(tweet.getMessage(), keyword))
                    .collect(Collectors.toList());
            homeTimelineCache.cacheTimeline(keyword, filteredTweets);
            return filteredTweets;
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

}
