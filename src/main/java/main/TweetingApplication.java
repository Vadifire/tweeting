package main;

//TODO: check imports
import io.dropwizard.Applicaiton;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.set.Environment;

public class TweetingApplication extends Application<TweetingConfiguration> {

	public static void main(String[] args) throws Exception {
		new TweetingApplication().run(args);
	}

	@Override
	public String getName() {
		return "tweeting";
	}

	@Override
	public void initialize(Bootstrap<TweetingConfiguration> bootstrap) {

	}

	@Override
	public void run(TweetingConfiguration config, Environment env) {

		/* Register Resources */

		final GetTimelineResource timelineResource = new GetTimelineResource();
		env.jersey().register(timelineResource);

		final PostTweetResource 
	}
}