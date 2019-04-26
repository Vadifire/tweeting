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
            if(updateStatus(updateText)){
                System.out.println("Successfully updated status.");
            }
        }
    }

    /*
     * Returns true iff successfully posted Status update
     */
    public static boolean updateStatus(String updateText) {
        if (updateText == null) {
            System.out.println("Could not update status to null String");
            return false;
        }
        if (updateText.length() > 280) {
            //TODO: verify how to handle posts with over 280 chars with requirements team
            System.out.println("Could not update status to String over 280 characters in length.");
            return false;
        }
        if (updateText.length() == 0) {
            System.out.println("Could not update status to 0 length String.");
            return false;
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(updateText);
        } catch (TwitterException e) {
            e.printStackTrace();
            System.out.println("Could not update status because connection to Twitter API failed.");
            return false;
        }
        return true;
    }
}
