package tweeting.services;

import twitter4j.util.CharacterUtil;

public enum TwitterErrorMessage {

    SERVICE_UNAVAILABLE("Service is temporarily unavailable."),
    NULL_TWEET("Could not post tweet because message parameter is missing."),
    INVALID_TWEET("Could not post tweet because message was either blank or longer than " +
                  CharacterUtil.MAX_TWEET_LENGTH + " characters.");

    TwitterErrorMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }

}
