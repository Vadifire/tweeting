/*
 * Representation Class for a Message
 *
 * JSON serialization is done by Jackson
 */

package main.api;

//TODO: check imports
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class Message {

    private long id;

    @Length(max = 280)
    private String content;

    public Message() {

    }

    public Message(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }

}