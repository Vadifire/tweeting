/*
 * Represents a post to be published on Twitter (text body only)
 *
 * Full Status objects are implemented by StatusJSONImpl.java in the Twitter4j API,
 * but this Post class supports simply specifying a String as the post body
 */

package main;

public class Post {

    private String text;

    public Post(String text) {
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
