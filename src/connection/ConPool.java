package connection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConPool {
	private List<MyCon> freeCons = new ArrayList<MyCon>();
	private List<MyCon> buzyCons = new ArrayList<MyCon>();
	private int max = 10;
	private int min = 1;
	private int current = 0;
	private static ConPool intance;

	private ConPool() {
		while (this.min > this.current) {
			this.freeCons.add(this.createCon());
		}
	}

	public static ConPool getIntance() {
		if (intance == null) {
			intance = new ConPool();
		}
		return intance;
	}

	public MyCon getCon() {
		MyCon myCon = this.getFreeCon();
		if (myCon != null) {
			return myCon;
		} else {
			return this.getNewCon();
		}
	}

	public MyCon getFreeCon() {
		if (freeCons.size() > 0) {
			MyCon con = freeCons.remove(0);
			con.setState(MyCon.BUZY);
			buzyCons.add(con);
			return con;
		} else {
			return null;
		}
	}

	public MyCon getNewCon() {
		if (current < max) {
			MyCon myCon = this.createCon();
			myCon.setState(MyCon.BUZY);
			buzyCons.add(myCon);
			return myCon;
		} else {
			return null;
		}
	}

	private MyCon createCon() {
		try {
			Connection connection = MySqlDao.getConnection();
			MyCon myCon = new MyCon(connection);
			current++;
			return myCon;
		} catch (Exception e) {
			return null;
		}
	}

	public void setFree(MyCon myCon) {
		buzyCons.remove(myCon);
		myCon.setState(MyCon.FREE);
		freeCons.add(myCon);
	}

	public String toString() {
		return "current connection:" + current + "\n" + "free connection:" + freeCons.size() + "\n" + "buzy connection:"
				+ buzyCons.size() + "\n";
	}

	// /**
	// * 测试代码
	// *
	// * @param args
	// */
	// public static void main(String[] args) {
	// try {
	// ConPool conPool = ConPool.getIntance();
	// MyCon myCon = conPool.getFreeCon();
	// System.out.println(myCon.START_DATE);
	// Thread.sleep(2000);
	// System.out.println(conPool.getNewCon().START_DATE);
	// conPool.setFree(myCon);
	// System.out.println(conPool.getFreeCon().START_DATE);
	// Statement statement = myCon.getConnection().createStatement();
	// statement.execute("select * from s");
	// ResultSet resultSet = statement.getResultSet();
	// while (resultSet.next()) {
	// System.out.println(resultSet.getString(1));
	// }
	// } catch (SQLException | InterruptedException e) {
	// e.printStackTrace();
	// }
	// System.out.println(ConPool.getIntance());
	// }
}
