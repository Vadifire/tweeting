package tweeting.conf;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TweetingConfiguration extends Configuration {

	@NotNull
	@JsonProperty
	@Valid
	private TwitterOAuthCredentials twitterOAuthCredentials;

	public void setAuthorization (TwitterOAuthCredentials authorization) {
		this.twitterOAuthCredentials = authorization;
	}

	public TwitterOAuthCredentials getAuthorization() {
		return twitterOAuthCredentials;
	}

}