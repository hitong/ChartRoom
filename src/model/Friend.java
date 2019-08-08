package model;

import javafx.beans.property.SimpleStringProperty;

public class Friend {
	private SimpleStringProperty userName;
	private SimpleStringProperty userId;
	private boolean history;

	public Friend(String userId, String userName) {
		this.userName = new SimpleStringProperty(userName);
		this.userId = new SimpleStringProperty(userId);
	}
	
	public boolean getHistory(){
		return history;
	}

	public void setHistory(boolean history){
		this.history = history;
	}
	
	public SimpleStringProperty getUserName() {
		return userName;
	}

	public SimpleStringProperty getUserId() {
		return userId;
	}
	
	public String toString(){
		return "id:  " + userId.getValue() + "  name:  " + userName.getValue();
	}
}
