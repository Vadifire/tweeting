package main;

/*
 * Used to represent error codes provided by Twitter
 *
 * See: https://developer.twitter.com/en/docs/basics/response-codes.html
 */

public enum TwitterErrorCode {

    AUTH_FAIL(32);

    private final int value;

    TwitterErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
