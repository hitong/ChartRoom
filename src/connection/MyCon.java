package connection;

import java.sql.Connection;
import java.util.Date;

public class MyCon {
	public final static int FREE = 100;//空闲信号
	public final static int BUZY = 101;//忙碌信号
	public final static int CLOSED = 102;//关闭信号
	public final Date START_DATE = new Date();
	private Connection connection;
	private int state = FREE;  //当前状态
	
	public MyCon(Connection connection){
		this.connection = connection;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
	}
}
