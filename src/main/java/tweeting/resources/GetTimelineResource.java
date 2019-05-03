package tweeting.resources;

import tweeting.util.ResponseUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTimelineResource {

    private Twitter api;

    ResponseUtil resUtil; // Provides messages for HTTP Responses, handles Twitter Exception

    public GetTimelineResource(Twitter api) {
        this.api = api;
        this.resUtil = new ResponseUtil("retrieve home timeline");
    }
    /*
     * How to use:
     * curl -i -X GET http://localhost:8080/api/1.0/twitter/timeline
     */
    @GET
    public Response getTweets() {
        try {
            List<Status> statuses = api.getHomeTimeline();
            if (statuses == null) { //this might never actually return true
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(resUtil.getNullResponse()).build();
            }
            return Response.ok(statuses).build(); // Successfully got timeline

        } catch (TwitterException e) {
            return resUtil.catchTwitterException(e);
        }
    }

    public ResponseUtil getResUtil () {
        return this.resUtil;
    }

}