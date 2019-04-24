package main;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class ProgramOne {

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Must input at least one String argument for posting.");
        }
        for (String updateText : args) {
            System.out.println(updateStatus(new Post(updateText)));
        }
    }

    /*
     * Returns true iff successfully posted Status update
     */
    public static boolean updateStatus(Post post) {
        if (post == null || post.getText() == null) {
            return false;
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(post.getText());
        } catch (TwitterException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
