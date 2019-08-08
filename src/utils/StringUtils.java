package utils;

public class StringUtils {
	public static String upperCaseFirthLatter(String str){
		return str.substring(0,1).toUpperCase() + str.substring(1);
	}
	
	public static String dbTypeTOJType(String str){
		switch (str) {
		case "VARCHAR":return "java.lang.String";
		case "SMALLINT": return "java.lang.Short";
		case "DATE": return "java.sql.Date";
		default:return str;
		}
	}
}
