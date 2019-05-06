package tweeting.resources;

import tweeting.util.ResponseUtil;
import tweeting.util.TwitterExceptionHandler;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.util.CharacterUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/tweet/")
public class PostTweetResource {

    /* Constants */
    public static final String MESSAGE_PARAM = "message"; // Used in ResponseUtil
    public static final String ATTEMPTED_ACTION = "retrieve home timeline";
    public static final String UNIT = "characters";
    public static final int MAX_TWEET_LENGTH = CharacterUtil.MAX_TWEET_LENGTH; // 280
    public static final int MIN_TWEET_LENGTH = 1;

    private Twitter api;

    private TwitterExceptionHandler exceptionHandler;

    public PostTweetResource(Twitter api) {
        this.api = api;
        setExceptionHandler(new TwitterExceptionHandler(ATTEMPTED_ACTION));
    }

    /*
     * How to use:
     * curl -i http://localhost:8080/api/1.0/twitter/tweet -d 'message=Hello World'
     *
     * Replace 'Hello World' with desired message.
     */
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postTweet(@FormParam(MESSAGE_PARAM) String message) { // Receives message from JSON data
        if (message == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ResponseUtil.getNullParamErrorMessage(ATTEMPTED_ACTION, MESSAGE_PARAM)).build();
        }

        if (message.length() > CharacterUtil.MAX_TWEET_LENGTH || message.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ResponseUtil.getParamBadLengthErrorMessage(ATTEMPTED_ACTION, MESSAGE_PARAM,
                            UNIT, 1, MAX_TWEET_LENGTH)).build();
        }

        try {
            Status returnedStatus = api.updateStatus(message); // Status should be updated to message

            // Return successful response with returned status
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
            responseBuilder.type(MediaType.APPLICATION_JSON);
            return responseBuilder.entity(returnedStatus).build();

        } catch (TwitterException e) {
            return exceptionHandler.catchTwitterException(e);
        }
	}

    /*
     * Used for mocking purposes
     */
    public void setExceptionHandler(TwitterExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}