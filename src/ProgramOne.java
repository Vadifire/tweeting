import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class ProgramOne {

    public static void main (String[] args){
        System.out.println(updateStatus("Update Status Test"));
    }


    /*
     * Returns false iff Twitter service or network is unavailable.
     */
    public static boolean updateStatus(String statusText){
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(statusText);
        } catch (TwitterException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
