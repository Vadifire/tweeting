package tweeting.services;

/*
 * Contains Twitter4J's functionality
 */
public class Twitter {
    private static final Twitter instance = new Twitter();

    private Twitter(){}

    public static Twitter getInstance() {
        return instance;
    }
}
