package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
	public static String getNowTime(){
		return format.format(new Date());
	}
}	
