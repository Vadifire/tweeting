package tweeting.models;

import java.util.Date;
import java.util.Optional;

public class Tweet {

    private String message;
    private TwitterUser user;
    private Date createdAt;

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Optional<TwitterUser> getUser() {
        return Optional.ofNullable(user);
    }

    public void setUser(TwitterUser user) {
        this.user = user;
    }

    public Optional<Date> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
