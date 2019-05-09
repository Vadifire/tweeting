package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ConsumerAPIKeys {

    @JsonProperty
    @NotEmpty
    @NotNull
    private String consumerAPIKey;

    public void setConsumerAPIKey(String consumerAPIKey) {
        this.consumerAPIKey = consumerAPIKey;
    }

    public String getConsumerAPIKey() {
        return consumerAPIKey;
    }

    @JsonProperty
    @NotEmpty
    @NotNull
    private String consumerAPISecretKey;

    public void setConsumerAPISecretKey(String consumerAPISecretKey) {
        this.consumerAPISecretKey = consumerAPISecretKey;
    }

    public String getConsumerAPISecretKey() {
        return consumerAPISecretKey;
    }
}
