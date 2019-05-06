package tweeting;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class TweetingConfiguration extends Configuration {

	@NotEmpty
	@JsonProperty
	private String host;

	@Min(1)
	@Max(65535)
	@JsonProperty
	@NotEmpty
	private int port;

	@JsonProperty
	@NotEmpty
	private String consumerKey;

	@JsonProperty
	@NotEmpty
	private String consumerSecret;

	@JsonProperty
	@NotEmpty
	private String accessToken;

	@JsonProperty
	@NotEmpty
	private String accessTokenSecret;

	/* Setters used for unit test of getHost() and getPort() */

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/* GETTERS BELOW */

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

}