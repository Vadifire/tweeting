package main;

//TODO: check imports
import main.resources.GetTimelineResource;
import main.resources.PostTweetResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TweetingApplication extends Application<TweetingConfiguration> {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Tweeting Service...");
		new TweetingApplication().run(args);
	}

	@Override
	public String getName() {
		return "tweeting";
	}

	@Override
	public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {
		System.out.println("Initializing Tweeting Service...");
	}

	@Override
	public void run(TweetingConfiguration config, Environment env) {
		System.out.println("Running Tweeting Service...");

		/* Register Resources */

		final GetTimelineResource timelineResource = new GetTimelineResource();
		env.jersey().register(timelineResource);

		/*final PostTweetResource tweetResource = new PostTweetResource(config.getTemplate(), config.getDefaultMessage());
		env.jersey().register(tweetResource);*/
	}
}