package utils;

public class NullTest {
	private static boolean string(String str) {
		if(str == "" || str.trim().equals("")) {
			return true;
		} 
		return false;
	}
	
	public static boolean isNull(Object obj){
		if(obj == null){
			return true;
		}
		if(obj.getClass().getName().equals("java.lang.String")){
			return string((String)obj);
		}
		else{
			return false;
		}
	}
}
