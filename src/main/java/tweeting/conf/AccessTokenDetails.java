package tweeting.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AccessTokenDetails {

    @JsonProperty
    @NotNull
    @NotEmpty
    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty
    @NotNull
    @NotEmpty
    private String accessTokenSecret;

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

}
