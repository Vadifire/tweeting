package tweeting.models;

import java.util.Optional;

public class TwitterUser {

    private String twitterHandle;
    private String name;
    private String profileImageUrl;

    public Optional<String> getTwitterHandle() {
        return Optional.ofNullable(twitterHandle);
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getProfileImageUrl() {
        return Optional.ofNullable(profileImageUrl);
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
