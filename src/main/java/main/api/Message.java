/*
 * Representation Class for a Message
 *
 * JSON serialization is done by Jackson
 */

package main.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class Message {

    private long id;

    @Length(min = 1, max = 50)
    private String name;

    @Length(min = 1, max = 280)
    private String text;

    public Message() {

    }

    public Message(long id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getText() {
        return text;
    }
}