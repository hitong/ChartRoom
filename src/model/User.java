package model;

public class User {
	private String name;//昵称
	private String userId;//账号
	private String psw;//密码
	
	public User(){
		this("", "", "");
	}

	public User(String userId, String name, String psw){
		this.userId = userId;
		this.name = name;
		this.psw = psw;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPsw() {
		return psw;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
}
