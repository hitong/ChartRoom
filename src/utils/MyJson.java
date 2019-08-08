package utils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJson {
	public static JSONObject changeCmd(String value1, String value2,String value3){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("value1", value1);
			jsonObject.put("value2", value2);
			jsonObject.put("value3", value3);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
}
