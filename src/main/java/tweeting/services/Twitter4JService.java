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

    private TimelineCache homeTimelineCache, userTimelineCache;

    private static final String EMPTY_FILTER = "";

    public Twitter4JService(Twitter api) {
        this.api = api;
        homeTimelineCache = new TimelineCache();
        userTimelineCache = new TimelineCache();
    }

    @Override
    public Optional<Tweet> postTweet(String message)
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
                        userTimelineCache.invalidate();
                        return tweet;
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }


    @Override
    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        try {
            final Optional<List<Tweet>> cachedTweets = homeTimelineCache.getCachedTimeline(EMPTY_FILTER);
            if (cachedTweets.isPresent()) {
                logger.info("Successfully retrieved home timeline from cache.");
                return cachedTweets;
            }
            final List<Tweet> tweets = api.getHomeTimeline()
                    .stream()
                    .map(this::constructTweet)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            homeTimelineCache.cacheTimeline(EMPTY_FILTER, tweets);
            logger.info("Successfully retrieved home timeline from Twitter.");
            return Optional.of(tweets);
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getUserTimeline() throws TwitterServiceResponseException {
        try {
            final Optional<List<Tweet>> cachedTweets = userTimelineCache.getCachedTimeline(EMPTY_FILTER);
            if (cachedTweets.isPresent()) {
                logger.info("Successfully retrieved user timeline from cache.");
                return cachedTweets;
            }
            final List<Tweet> tweets = api.getUserTimeline()
                    .stream()
                    .map(this::constructTweet)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            userTimelineCache.cacheTimeline(EMPTY_FILTER, tweets);
            logger.info("Successfully retrieved user timeline from Twitter.");
            return Optional.of(tweets);
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getFilteredTimeline(String keyword)
            throws TwitterServiceResponseException, TwitterServiceCallException {
        if (StringUtils.isBlank(keyword)) {
            throw new TwitterServiceCallException(MISSING_KEYWORD_MESSAGE);
        }
        // Try to pull filtered result from timelineCache.
        final Optional<List<Tweet>> cachedTweets = homeTimelineCache.getCachedTimeline(keyword);
        if (cachedTweets.isPresent()) {
            logger.info("Successfully retrieved filtered home timeline from cache.");
            return cachedTweets;
        } // Otherwise, make call to getHomeTimeline()
        final List<Tweet> filteredTweets = getHomeTimeline()
                .get()
                .stream()
                .filter(tweet -> StringUtils.containsIgnoreCase(tweet.getMessage(), keyword))
                .collect(Collectors.toList());
        homeTimelineCache.cacheTimeline(keyword, filteredTweets);
        return Optional.of(filteredTweets);
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
            user.setProfileImageUrl(status.getUser().get400x400ProfileImageURL());
            tweet.setUser(user);
            tweet.setUrl(TWITTER_BASE_URL + status.getUser().getScreenName() + STATUS_DIRECTORY + status.getId());
        }
        tweet.setCreatedAt(status.getCreatedAt());
        return tweet;
    }

}
