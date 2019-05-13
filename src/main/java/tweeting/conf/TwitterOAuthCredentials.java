package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TwitterOAuthCredentials {

    @NotNull
    @JsonProperty
    @Valid
    private AccessTokenDetails accessTokenDetails;

    public void setAccessTokenDetails(AccessTokenDetails accessTokenDetails) {
        this.accessTokenDetails = accessTokenDetails;
    }

    public AccessTokenDetails getAccessTokenDetails() {
        return accessTokenDetails;
    }

    @NotNull
    @JsonProperty
    @Valid
    private ConsumerAPIKeys consumerAPIKeys;

    public void setConsumerAPIKeys(ConsumerAPIKeys consumerAPIKeys) {
        this.consumerAPIKeys = consumerAPIKeys;
    }

    public ConsumerAPIKeys getConsumerAPIKeys() {
        return consumerAPIKeys;
    }

}
