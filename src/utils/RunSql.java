package utils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import connection.ConPool;
import connection.MyCon;
import other.Entity;


public class RunSql {
	public static boolean returnBool(String sql) {
		ConPool conPool = ConPool.getIntance();
		MyCon myCon = conPool.getCon();
		Statement statement;
		boolean flag = false;
		try {
			statement = myCon.getConnection().createStatement();
			statement.execute(sql);
			if(statement.getUpdateCount() > 0){
				flag = true;
			}
			else{
				ResultSet resultSet = statement.getResultSet();
				if(resultSet != null){
					if(resultSet.next()){
						flag = true;
					}
					resultSet.close();
				}
			}
			statement.close();
			conPool.setFree(myCon);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	public static ArrayList<Object> returnArray(Object obj, Entity entity,String sql) {
		ConPool conPool = ConPool.getIntance();
		MyCon myCon = conPool.getCon();
		Statement statement;
		ArrayList<Object> arrayList = null;
		try {
			statement = myCon.getConnection().createStatement();
			statement.execute(sql);
			ResultSet resultSet = statement.getResultSet();
			arrayList = ResultToArray.change(obj, entity, resultSet);
			resultSet.close();
			statement.close();
			conPool.setFree(myCon);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return arrayList;
	}
	
}
