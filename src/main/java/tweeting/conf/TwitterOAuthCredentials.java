package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class TwitterOAuthCredentials {

    @NotNull
    @JsonProperty
    private AccessTokenDetails accessTokenDetails;

    public void setAccessTokenDetails(AccessTokenDetails accessTokenDetails) {
        this.accessTokenDetails = accessTokenDetails;
    }

    public AccessTokenDetails getAccessTokenDetails() {
        return accessTokenDetails;
    }

    @NotNull
    @JsonProperty
    private ConsumerAPIKeys consumerAPIKeys;

    public void setConsumerAPIDetails(ConsumerAPIKeys consumerAPIKeys) {
        this.consumerAPIKeys = consumerAPIKeys;
    }

    public ConsumerAPIKeys getConsumerAPIDetails() {
        return consumerAPIKeys;
    }

}
