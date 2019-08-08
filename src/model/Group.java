package model;

public class Group {
	private String groupId;
	private String userId;
	
	public Group(String groupId, String userId) {
		super();
		this.groupId = groupId;
		this.userId = userId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
