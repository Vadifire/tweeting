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
	private int port;

	/* Setters used for unit test of getHost() and getPort() */
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}