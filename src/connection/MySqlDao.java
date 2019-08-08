package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlDao {
	public static Connection getConnection() throws Exception {
		String driverName = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/chart_room?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
		String userName = "root";
		String userPwd = "w1906194855";
		Class.forName(driverName);
		return DriverManager.getConnection(url, userName, userPwd);
	}
}
