package jfullam.vfabric.jenkins.plugin.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * Encapsulates the response from calling the 
 * Application Director API.  This is just the generic part
 * of the response content that contains information on whether
 * the call was had any errors.
 * 
 * @author Jonathan Fullam
 */
public class ServiceResult {

	private boolean success;
	private List<String> messages;
	
	public ServiceResult(boolean success, List<String> messages) {
		super();
		this.success = success;
		this.messages = messages;
	}

	public ServiceResult(boolean success, String msg) {
		ArrayList<String> msgList = new ArrayList<String>();
		msgList.add(msg);
		this.success = success;
		this.messages = msgList;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	/**
	 * Parses the JsonNode into a ServiceResult object
	 * 
	 * @param jsonResult
	 * @return ServiceResult 
	 */
	public static ServiceResult parseJson(JsonNode jsonResult) {
		boolean failure = jsonResult.get("errors").getBooleanValue();
		ArrayList<String> msgList = new ArrayList<String>();
		Iterator<JsonNode> messages = jsonResult.get("messageList").getElements();
		
		while(messages.hasNext()) {
			JsonNode jsonMsg = messages.next();
			String message = jsonMsg.get("messageType").asText() + ":  " + jsonMsg.get("message").asText();
			msgList.add(message);
		}
		
		return new ServiceResult(!failure,msgList);
	}
	
	
	
}
