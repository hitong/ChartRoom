package other;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connection.GenneralRunSql;
import utils.RunSql;
import utils.XmlReaderFromDB;

public class GenneralBaseDao <T>{
	public final static HashMap<String, Entity> entities = new HashMap<String, Entity>();
	static {
		ArrayList<Entity> tmpArr = new ArrayList<Entity>();
		try {
			XmlReaderFromDB.readDB("D:/JavaWorkplace/ChartRoom/src/db.xml", tmpArr);
			for (Entity entity : tmpArr) {
				entities.put(entity.getClassName(), entity);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// 将数据库表信息加载进哈希表中

	
	/**
	 * 将对象信息从数据库中删除
	 * @param obj
	 * @return
	 */
	public boolean deleteObj(T obj) {
		if (utils.NullTest.isNull(obj)) {
			return false;
		}
		Entity entity =  entities.get(obj.getClass().getName());
		List<EntityField> keyFields = entity.getKeyFileds();
		try {
			StringBuilder tmp = new StringBuilder("");
			for(int i = 0; i < keyFields.size(); i++){
				Method method = obj.getClass()
						.getMethod("get" + utils.StringUtils.upperCaseFirthLatter(keyFields.get(i).getFieldName()));
				tmp.append(keyFields.get(i).getColumn() + "='"+ method.invoke(obj) + "' ,");
			}
			if(keyFields.size() > 0){
				tmp.deleteCharAt(tmp.length() - 1);
				String sql = "delete from " + entity.getTable() + " where " + tmp;
				return  RunSql.returnBool(sql);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将对象信息更新进数据库
	 * @param obj
	 * @return
	 */
	public boolean updateObj(T obj) {
		if (utils.NullTest.isNull(obj)) {
			return false;
		}
		Entity entity =  entities.get(obj.getClass().getName());
		List<EntityField> keyFields = entity.getKeyFileds();
		try {
			StringBuilder tmp = new StringBuilder("");
			for(int i = 0; i < keyFields.size(); i++){
				Method method = obj.getClass()
						.getMethod("get" + utils.StringUtils.upperCaseFirthLatter(keyFields.get(i).getFieldName()));
				tmp.append(keyFields.get(i).getColumn() + "='"+ method.invoke(obj) + "' ,");
			}
			if(keyFields.size() > 0){
				tmp.deleteCharAt(tmp.length() - 1);
				StringBuilder value = new StringBuilder();
				Method method;
				for (EntityField entityField : entity.getFields()) {
					method = obj.getClass()
							.getMethod("get" + utils.StringUtils.upperCaseFirthLatter(entityField.getFieldName()));
					value.append(entityField.getColumn() + " = " + "'" + method.invoke(obj) + "' ,");
				}
				value.deleteCharAt(value.length() - 1);
				String sql = "update " + entity.getTable() + " set " + value + " where " + tmp;
				return RunSql.returnBool(sql);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加对象信息进入数据库
	 * 
	 * @param obj
	 * @return
	 */
	public boolean addObj(T obj) {
		if (utils.NullTest.isNull(obj)) {
			return false;
		}
		Entity entity =  entities.get(obj.getClass().getName());
		boolean flag = false;
		try {
			StringBuilder tmpValue = new StringBuilder("(");
			StringBuilder tmpColumn = new StringBuilder("(");
			Method method;
			
			for (EntityField entityField : entity.getFields()) {
				method = obj.getClass()
						.getMethod("get" + utils.StringUtils.upperCaseFirthLatter(entityField.getFieldName()));
				tmpValue.append("'" + method.invoke(obj) + "' ,");
				tmpColumn.append(entityField.getColumn() + " ,");
				flag = true;
			}
			if (flag) {
				tmpValue.deleteCharAt(tmpValue.length() - 1);
				tmpValue.append(")");
				tmpColumn.deleteCharAt(tmpColumn.length() - 1);
				tmpColumn.append(")");
			}
			String sql = "insert into " + entity.getTable() + tmpColumn + " values" + tmpValue;
			flag = RunSql.returnBool(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 查询该对象是否在对应的数据库中
	 * 
	 * @param obj
	 * @return
	 */
	public boolean selectObj(T obj) {
		if (utils.NullTest.isNull(obj)) {
			return false;
		}
		
		Entity entity =  entities.get(obj.getClass().getName());
		List<EntityField> keyFields = entity.getKeyFileds();
		try {
			StringBuilder tmp = new StringBuilder("");
			for(int i = 0; i < keyFields.size(); i++){
				Method method = obj.getClass()
						.getMethod("get" + utils.StringUtils.upperCaseFirthLatter(keyFields.get(i).getFieldName()));
				tmp.append(keyFields.get(i).getColumn() + "='"+ method.invoke(obj) + "' ,");
			}
			if(keyFields.size() > 0){
				tmp.deleteCharAt(tmp.length() - 1);
				String sql = "select * from " + entity.getTable() + " where " + tmp;
				return RunSql.returnBool(sql);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 传入类型信息，以及查询sql语句，返回对应类型的对象列表
	 * @param obj
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<T> queryObj(T obj, String sql) {
		if(utils.NullTest.isNull(obj) || utils.NullTest.isNull(sql)){
			return null;
		}
		Entity entity =  entities.get(obj.getClass().getName());
		return (ArrayList<T>) GenneralRunSql.returnArray(obj.getClass(),entity,sql);
	}
}
