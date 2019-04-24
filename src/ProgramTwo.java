import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class ProgramTwo {

    public static void main(String args[]){
        List<Status> statuses = getHomeTimelineStatuses();
        printStasues(statuses);
    }

    /*
     * Returns List of Status objects from Home Timeline
     * Returns null if TwitterException occurs when trying to retrieve statuses
     */
    public static List<Status> getHomeTimelineStatuses(){
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            List<Status> statuses = twitter.getHomeTimeline();
            return statuses;
        } catch (TwitterException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void printStasues(List<Status> statuses){
        if (statuses == null){
            return;
        }
        for (Status status : statuses){
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }
    }
}
