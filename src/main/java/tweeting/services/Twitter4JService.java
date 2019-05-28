package tweeting.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tweeting.models.Tweet;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tweeting.services.Twitter4JUtil.constructTweet;

@Singleton
public class Twitter4JService implements TwitterService {

    private final Twitter api;

    private static final Logger logger = LoggerFactory.getLogger(Twitter4JService.class);

    private TimelineCache homeTimelineCache;

    public Twitter4JService(Twitter api, TimelineCache homeTimelineCache) {
        this.api = api;
        this.homeTimelineCache = homeTimelineCache;
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
                        homeTimelineCache.pushTweet(tweet); // Update cacheTimeline
                        return tweet;
                    });
        } catch (TwitterException te) {
            throw createServerException(te);
        }
    }

    @Override
    public Optional<List<Tweet>> getHomeTimeline() throws TwitterServiceResponseException {
        if (homeTimelineCache.isFresh()) {
            logger.info("Successfully retrieved home timeline from cache.");
            return Optional.of(homeTimelineCache.getCachedTimeline());
        }
        try {
            return Optional.ofNullable(api.getHomeTimeline())
                    .map(statuses -> {
                        final List<Tweet> tweets = Twitter4JUtil.constructTweetList(statuses);
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
            if (homeTimelineCache.canGetFilteredTimeline(keyword)) {
                logger.info("Successfully retrieved home timeline filtered by \'" + keyword + "\' from cache.");
                return Optional.of(homeTimelineCache.getCachedFilteredTimeline(keyword));
            }
            // Next, attempt to retrieve timeline from cache and apply filter
            else if (homeTimelineCache.isFresh()) {
                final List<Tweet> tweets = homeTimelineCache.getCachedTimeline()
                        .stream()
                        .filter(tweet -> StringUtils.containsIgnoreCase(tweet.getMessage(), keyword))
                        .collect(Collectors.toList());
                homeTimelineCache.cacheFilteredTimeline(keyword, tweets);
                logger.info("Successfully retrieved home timeline from cache and filtered by \'" + keyword + "\'.");
                return Optional.ofNullable(tweets);
            }
            // Otherwise, need to pull from Twitter.
            final List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) {
                return Optional.empty();
            }
            homeTimelineCache.cacheStatuses(statuses);
            final List<Tweet> tweets = statuses.stream()
                    .filter(status -> StringUtils.containsIgnoreCase(status.getText(), keyword))
                    .map(Twitter4JUtil::constructTweet)
                    .collect(Collectors.toList());
            homeTimelineCache.cacheFilteredTimeline(keyword, tweets);
            logger.info("Successfully retrieved home timeline from Twitter and filtered by \'" + keyword + "\'.");
            return Optional.ofNullable(tweets);
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
}
