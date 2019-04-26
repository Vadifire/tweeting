package main.resources;

//TODO check imports
import main.api.Message;
import main.ProgramTwo;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

@Path("/api/1.0/twitter/timeline")
@Produces(MediaType.APPLICATION_JSON)

public class GetTweetsResource {
	private final AtomicLong counter; //Provides thread-safe unique(ish) ID

    public GetTimelineResource() {
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Timeline getTweets() { //TODO: add error checking
    	List<String> value;
    	List<Status> statuses = ProgramTwo.getHomeTimelineStatuses();
    	if (statuses == null){
    		//TODO: exception
    	}
    	for (Status status : statuses) {
    		value.add(status.getText());
    	}
    	return new Timeline(counter.incrementAndGet(), value);
    }

}