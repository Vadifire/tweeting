package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AuthorizationDetails {

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
    private ConsumerAPIDetails consumerAPIKeys;

    public void setConsumerAPIDetails(ConsumerAPIDetails consumerAPIDetails) {
        this.consumerAPIKeys = consumerAPIDetails;
    }

    public ConsumerAPIDetails getConsumerAPIDetails() {
        return consumerAPIKeys;
    }

}
