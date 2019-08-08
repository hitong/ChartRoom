package connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import other.Entity;
import utils.GenneralResultToArray;


public class GenneralRunSql {
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
	
	public static <T> ArrayList<T> returnArray(Class<T> clazz, Entity entity,String sql) {
		ConPool conPool = ConPool.getIntance();
		MyCon myCon = conPool.getCon();
		Statement statement;
		ArrayList<T> arrayList = null;
		try {
			statement = myCon.getConnection().createStatement();
			statement.execute(sql);
			ResultSet resultSet = statement.getResultSet();
			arrayList = GenneralResultToArray.change(clazz, entity, resultSet);
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
