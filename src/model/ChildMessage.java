package model;

public class ChildMessage {
	private String messageId;
	private String meg;
	
	public ChildMessage(){
		this("","");
	}
	
	public ChildMessage(String messageId, String meg){
		this.messageId = messageId;
		this.meg = meg;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMeg() {
		return meg;
	}
	public void setMeg(String meg) {
		this.meg = meg;
	}
}
