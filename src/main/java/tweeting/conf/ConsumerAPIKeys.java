package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ConsumerAPIKeys {

    @JsonProperty
    @NotEmpty
    private String consumerAPIKey;

    public void setConsumerAPIKey(String consumerAPIKey) {
        this.consumerAPIKey = consumerAPIKey;
    }

    public String getConsumerAPIKey() {
        return consumerAPIKey;
    }

    @JsonProperty
    @NotEmpty
    private String consumerAPISecretKey;

    public void setConsumerAPISecretKey(String consumerAPISecretKey) {
        this.consumerAPISecretKey = consumerAPISecretKey;
    }

    public String getConsumerAPISecretKey() {
        return consumerAPISecretKey;
    }
}
