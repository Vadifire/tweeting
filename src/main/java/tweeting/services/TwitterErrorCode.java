package tweeting.services;

/*
 * Twitter Error Codes (https://developer.twitter.com/en/docs/basics/response-codes.html)
 */
public enum TwitterErrorCode {

    BAD_AUTH_DATA(215), COULD_NOT_AUTH(32), MESSAGE_BLANK(170), MESSAGE_TOO_LONG(186);

    TwitterErrorCode(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}