package main;

//TODO: check imports
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class TweetingConfiguration extends Configuration {

	@NotEmpty 
	private String template;

	@NotEmpty
	private String defaultMessage = "Hello World";

	@JsonProperty
	public String getTemplate() {
		return template;
	}

	@JsonProperty
	public void setTemplate() {
		this.template = template;
	}

	@JsonProperty
	public String getDefaultMessage() {
		return defaultMessage;
	}

	@JsonProperty
	public void setDefaulMessage(String message){
		this.defaultMessage = message;
	}
}