package main;

import main.resources.PostTweetResource;

public class ProgramOne {

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Must input at least one String argument for posting.");
            return;
        }
        for (String updateText : args) {
            if(PostTweetResource.updateStatus(updateText)){
                System.out.println("Successfully updated status.");
            }
        }
    }
}
