package tweeting.resources;

import tweeting.util.ResponseUtil;
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

    private final String messageParam = "message"; // Used in ResponseUtil

    private Twitter api;

    ResponseUtil resUtil; // Provides messages for HTTP Responses, handles TwitterException

    public PostTweetResource(Twitter api) {
        this.api = api;
        resUtil = new ResponseUtil("post tweet");
    }

    /*
     * How to use:
     * curl -i http://localhost:8080/api/1.0/twitter/tweet -d 'message=Hello World'
     *
     * Replace 'Hello World' with desired message.
     */
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postTweet(@FormParam(messageParam) String message) { // Receives message from JSON data
        if (message == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(resUtil.getNullParamError(messageParam)).build();
        }

        if (message.length() > CharacterUtil.MAX_TWEET_LENGTH) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(resUtil.getParamTooLongError(messageParam, "characters",
                            CharacterUtil.MAX_TWEET_LENGTH)).build();
        }
        if (message.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(resUtil.getParamEmptyError(messageParam)).build();
        }

        try {
            Status returnedStatus = api.updateStatus(message); // Latest status should be updated to message

            if (returnedStatus.getText().equals(message)) { // Status with correct text was posted

                // Return successful response with latest status
                Response.ResponseBuilder responseBuilder = Response.status(Response.Status.CREATED);
                responseBuilder.type(MediaType.APPLICATION_JSON);
                return responseBuilder.entity(returnedStatus).build();
                
            } else { // Status with wrong text was posted

                api.destroyStatus(returnedStatus.getId()); // Send delete request to Twitter
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(resUtil.getIncorrectUpdateError("tweet")).build();
            }

        } catch (TwitterException e) {
            return resUtil.catchTwitterException(e);
        }
	}

	public ResponseUtil getResUtil() {
	    return resUtil;
    }
}