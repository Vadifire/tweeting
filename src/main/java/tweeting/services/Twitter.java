package tweeting.services;

/*
 * Contains Twitter4J's functionality
 */
public class Twitter {
    
    private static Twitter instance;

    private Twitter(){}

    public static Twitter getInstance() {
        if (instance == null) {
            return new Twitter();
        }
        return instance;
    }
}
