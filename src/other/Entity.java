package other;
import java.util.ArrayList;
import java.util.List;
	
	
public class Entity {
	String className;
	String table;
	ArrayList<EntityField> fields=new ArrayList<EntityField>();
	public String getClassName() {
	return className;
	}
	public void setClassName(String className) {
	this.className = className;
	}
	public String getTable() {
	return table;
	}
	public void setTable(String table) {
	this.table = table;
	}
	public ArrayList<EntityField> getFields() {
	return fields;
	}
	public void setFields(ArrayList<EntityField> fields) {
	this.fields = fields;
	}
	public List<EntityField> getKeyFileds(){
	ArrayList<EntityField> tmp = new ArrayList<>();
	for(int i=0;i<fields.size();i++){
	   if(fields.get(i).isKey){
		   tmp.add(fields.get(i));
	   }
	}
	return tmp;
	}
}
