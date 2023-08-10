package com.vivatelecoms.greenzone.wapchatezee.model;

public class VoiceMailDetails {

	private String messageId;
	private String subscriberId;
	private String duration;
	private String sendDate;
	private String recordingPath;
	
	public VoiceMailDetails() {
		messageId="";
		subscriberId="";
		duration="";
		sendDate="";
		recordingPath="";
	}
	
	
	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}


	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getRecordingPath() {
		return recordingPath;
	}
	public void setRecordingPath(String recordingPath) {
		this.recordingPath = recordingPath;
	}
	
}
