import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class ProgramOne {

    public static void main (String[] args){
        System.out.println(updateStatus(new Post("post test")));
    }


    /*
     * Returns false iff Twitter service or network is unavailable, returns true otherwise.
     */
    public static boolean updateStatus(Post post){

        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(post.getText());
        } catch (TwitterException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

/*
 * Represents a post to be published on Twitter (text body only)
 *
 * Full Status objects are implemented by StatusJSONImpl.java in the Twitter4j API,
 * but this Post class supports simply specifying a String as the post body
 */
class Post {

    private String text;


    public Post(String text) {
        this.text = text;
    }

    public String getText(){
        return text;
    }
}