package main.resources;

//TODO check imports
import main.api.Message;
import main.ProgramTwo;
import main.Post;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

import java.util.concurrent.atomic.AtomicLong;

//@Path("/api/1.0/twitter/tweet")
//@Produces(MediaType.APPLICATION_JSON)

public class PostTweetResource {
/*
	private final String template;
	private final String defaultMessage;
	private final AtomicLong counter; //provides thread-safe unique ID

	public PostTweetResource(String template, String defaultMessage){
		this.template = template;
		this.defaultMessage = defaultMessage;
		this.counter = new AtomicLong();
	}

	@POST
	@Timed
	public boolean postTweet(@PathParam("message") StringParam message) { // Change to return HTTP response?

		return ProgramTwo.updateStatus(new Post(message)); //TODO: remove Post class
		
	}*/
}