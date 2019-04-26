/* 
 * Representation Class for a Timeline
 *
 * JSON serialization is done by Jackson
 */

package main.api;

//TODO: check imports
import com.fasterxml.jackson.annotation.JsonProperty;

public class Timeline {

	private long id;

	private List<String> messages; //TODO: Pick whether this should be String or Message

	public Timeline() {
		
	}

	public Timeline(long id, List<String> messages) {
		this.id = id;
		this.messages = messages;
	}

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public List<String> getMessages() {
        return messages;
    }

}