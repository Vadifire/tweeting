import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class ProgramTwo {

    public static void main(String args[]){
        printHomeTimeline();
    }

    /*
     * Returns false iff Twitter service or network is unavailable.
     */
    public static boolean printHomeTimeline(){
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            List<Status> statuses = twitter.getHomeTimeline();
            System.out.println("Printing Home Timeline...");
            for (Status status : statuses){
                System.out.println(status.getUser().getName() + ":" +
                        status.getText());
            }
        } catch (TwitterException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
