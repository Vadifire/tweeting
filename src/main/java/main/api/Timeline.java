/* 
 * Representation Class for a Timeline
 *
 * JSON serialization is done by Jackson
 */

package main.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Timeline {

	private List<Message> content; //List of messages

	public Timeline() {
		
	}

	public Timeline(List<Message> messages) {
		this.content = messages;
	}

    @JsonProperty
    public List<Message> getContent() {
        return content;
    }

}