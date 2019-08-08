package model;

import java.util.ArrayList;

import other.GenneralBaseDao;

public class RelationService {
	GenneralBaseDao<Relation> bd = new GenneralBaseDao<Relation>();
	private static  RelationService ralationServervice = new RelationService();
	
	private RelationService(){
	}
	
	public static RelationService getInstance(){
		return ralationServervice;
	}
	
	public boolean addRelation(Relation relation){
		return bd.addObj(relation);
	}
	
	public boolean delRelation(Relation relation){
		return bd.deleteObj(relation);
	}
	
	public ArrayList<Relation> getRelations(String userId){
		return bd.queryObj(new Relation(), "select * from relation where  UserAId = '" + userId + "' or UserBId = '" + 
				userId+ "'");
	}
	
	public static String getFriendId(Relation relation,String user){
		return relation.getUserAId().equals(user) ? relation.getUserBId() : relation.getUserAId();
	}
}
