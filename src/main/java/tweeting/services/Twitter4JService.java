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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class Twitter4JService implements TwitterService {

    private final Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    private TimelineCache homeTimelineCache; // Used to avoid making extra API calls to Twitter
    private FilteredTimelineCache filteredCache; // Used to avoid making extra API calls to Twitter

    public Twitter4JService(Twitter api, TimelineCache homeTimelineCache, FilteredTimelineCache filteredCache) {
        this.api = api;
        this.homeTimelineCache = homeTimelineCache;
        this.filteredCache = filteredCache;
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
                        filteredCache.invalidate();
                        return tweet;
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        if (homeTimelineCache.getCachedTimeline() != null) {
            logger.info("Successfully retrieved home timeline from cache.");
            return Optional.of(homeTimelineCache.getCachedTimeline());
        }
        try {
            return Optional.ofNullable(api.getHomeTimeline())
                    .map(statuses -> {
                        final List<Tweet> tweets = Twitter4JService.constructTweetList(statuses);
                        homeTimelineCache.cacheTweets(tweets);
                        logger.info("Successfully retrieved home timeline from Twitter.");
                        return tweets;
                    });
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
        try {
            // First attempt to retrieve from filtered cache
            if (filteredCache.containsKeyword(keyword)) {
                logger.info("Successfully retrieved home timeline filtered by \'" + keyword + "\' from cache.");
                return Optional.of(filteredCache.getTweets(keyword));
            }
            // Next, attempt to retrieve timeline from cache and apply filter
            else if (homeTimelineCache.getCachedTimeline() != null) {
                final List<Tweet> tweets = homeTimelineCache.getCachedTimeline()
                        .stream()
                        .filter(tweet -> StringUtils.containsIgnoreCase(tweet.getMessage(), keyword))
                        .collect(Collectors.toList());
                filteredCache.cacheTweets(keyword, tweets);
                logger.info("Successfully retrieved home timeline from cache and filtered by \'" + keyword + "\'.");
                return Optional.of(tweets);
            }
            // Otherwise, need to pull from Twitter.
            final List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) {
                return Optional.empty();
            }
            final List<Tweet> tweets = constructTweetList(statuses);
            homeTimelineCache.cacheTweets(tweets);
            logger.info("Successfully retrieved home timeline from Twitter and filtered by \'" + keyword + "\'.");
            return Optional.of(tweets.stream()
                    .filter(tweet -> StringUtils.containsIgnoreCase(tweet.getMessage(), keyword))
                    .collect(Collectors.toList()));
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    private TwitterServiceResponseException createServerException(TwitterException te) {
        if (te.isCausedByNetworkIssue() || te.getErrorCode() == TwitterErrorCode.BAD_AUTH_DATA.getCode() ||
                te.getErrorCode() == TwitterErrorCode.COULD_NOT_AUTH.getCode()) {
            return new TwitterServiceResponseException(SERVICE_UNAVAILABLE_MESSAGE, te);
        } else {
            return new TwitterServiceResponseException(te);
        }
    }

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
                .map(Twitter4JService::constructTweet)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
