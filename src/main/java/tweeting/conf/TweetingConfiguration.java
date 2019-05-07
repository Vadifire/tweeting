package tweeting.conf;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class TweetingConfiguration extends Configuration {

	@NotNull
	@JsonProperty
	private AuthorizationDetails authorization;

	public void setAuthorization (AuthorizationDetails authorization) {
		this.authorization = authorization;
	}

	public AuthorizationDetails getAuthorization() {
		return authorization;
	}

}