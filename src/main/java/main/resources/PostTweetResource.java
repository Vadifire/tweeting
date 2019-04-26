package main.resources;

//TODO check imports
import main.ProgramOne;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

@Path("/api/1.0/twitter/tweet/{message}")
@Consumes(MediaType.APPLICATION_JSON)
public class PostTweetResource {

	private final String template;
	private final String defaultMessage;
	private final AtomicLong counter; //provides thread-safe unique ID

	public PostTweetResource(String template, String defaultMessage) {
		this.template = template;
		this.defaultMessage = defaultMessage;
		this.counter = new AtomicLong();
	}

	@POST
	@Timed
	public boolean postTweet(@PathParam("message") String message) { // Change to return HTTP response?
		return ProgramOne.updateStatus(message); //TODO: remove Post class
	}

	/*
	 * How to use: //TODO: make this more readable
	 * curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/api/1.0/twitter/tweet/message
	 *
	 * Replace 'message' with message desired.
     */
}