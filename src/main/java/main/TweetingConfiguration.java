package main;

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
	private int port;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}