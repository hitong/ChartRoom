package model;

public class Relation {
	private String userAId;
	private String userBId;
	
	public Relation(){
		this("", "");
	}
	
	public Relation(String str1, String str2) {
		super();
		if(str1.compareTo(str2) > 0){
			userAId = str2;
			userBId = str1;
		} else{
			userBId = str2;
			userAId = str1;
		}
	}
	public String getUserAId() {
		return userAId;
	}
	public void setUserAId(String userAId) {
		this.userAId = userAId;
	}
	public String getUserBId() {
		return userBId;
	}
	public void setUserBId(String userBId) {
		this.userBId = userBId;
	}
	
	
}
