/* 
 * Representation Class for a Timeline
 *
 * JSON serialization is done by Jackson
 */

package main.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Timeline {

	private long id;

	private List<Message> content; //List of messages

	public Timeline() {
		
	}

	public Timeline(long id, List<Message> messages) {
		this.id = id;
		this.content = messages;
	}

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public List<Message> getContent() {
        return content;
    }

}