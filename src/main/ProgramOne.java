package main;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class ProgramOne {

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Must input at least one String argument for posting.");
            return;
        }
        for (String updateText : args) {
            if(updateStatus(new Post(updateText))){
                System.out.println("Successfully updated status.");
            }
        }
    }

    /*
     * Returns true iff successfully posted Status update
     */
    public static boolean updateStatus(Post post) {
        if (post == null || post.getText() == null) {
            System.out.println("Could not update status without valid Post object containing text.");
            return false;
        }
        if (post.getText().length() > 280) {
            //TODO: verify how to handle posts with over 280 chars with requirements team
            System.out.println("Could not update status because post contained over 280 characters.");
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
