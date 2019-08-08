package model;

import java.util.ArrayList;

import other.GenneralBaseDao;

public class UserService {
	private GenneralBaseDao<User> bd = new GenneralBaseDao<User>();
	private static UserService userService = new UserService();
	
	private UserService(){
	}

	public static UserService getInstance(){
		return userService;
	}
	public boolean addUser(User user) {
		return bd.addObj(user);
	}

	public boolean updateUser(User user) {
		return bd.updateObj(user);
	}
	
	public ArrayList<User> searchUser(String search){
		return bd.queryObj(new User(), "select * from User where UserId = '" + search + "'");
	}
	
	public User selectUser(String userId){
		try{
			return bd.queryObj(new User(), "select * from user where UserId = '" + userId + "'").get(0);
		} catch(Exception ex){
			return null;
		}
	}
	
	public boolean verificationUser(String userName, String psw){
		String sql = "Select * from user where userId = " + "'" + userName + "'and password = '" + psw + "'";
		User tmp = new User(userName,"",psw);
		return bd.queryObj(tmp, sql).size() == 1;
	}

	public ArrayList<User> queryUser() {
		return bd.queryObj(new User(), "select * from User");
	}
}
