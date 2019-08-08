package model;

import java.util.ArrayList;

import other.GenneralBaseDao;

public class CMService {
	private static GenneralBaseDao<ChildMessage> bd = new GenneralBaseDao<ChildMessage>();
	private static CMService cmService = new CMService();
	
	public static CMService getInstance(){
		return cmService;
	}
	
	public ChildMessage getCM(String messageId){
		ArrayList<ChildMessage> tmp = bd.queryObj(new ChildMessage(), "select * from childMessage where MessageId = '" + messageId + "'");
		if(tmp.size() == 0){
			return null;
		} else {
			return tmp.get(0);
		}
	}
	
	public boolean putCM(ChildMessage childMessage){
		if(childMessage == null){
			return true;
		}
		return bd.addObj(childMessage);
	}
	
	
	public boolean delCM(String childMessage){
		if(childMessage == null){
			return true;
		}
		return bd.deleteObj(new ChildMessage(childMessage,""));
	}
}
