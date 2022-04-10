package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import common.Cordinate;
import common.Parameters;
import common.SigRecord;

/**
 * 用于连接数据库获取数据集，进行处理后返回可供使用的数据结构
 * @author 姜云骞
 */
public class SqlPerformImp implements SqlPerform {

	/*
	 * ACCESS 数据库：DB_DRIVER = "com.hxtt.sql.access.AccessDriver";
	 * 				url = "jdbc:access:/"+new File("").getAbsolutePath().replace('\\', '/') + "[name]";
	 * 				user="";
	 *				passwd=null;
	 */
	private static final String DB_DRIVER = "com.hxtt.sql.access.AccessDriver";
	private static String url = "jdbc:access:/"+new File("").getAbsolutePath().replace('\\', '/') + "/p&c.accdb";
	String user="";
	String passwd=null;

	@Override
	public Vector<SigRecord> allRecords(String sql, String[] paras) {
		PreparedStatement ps=null;
		Connection connection=null;
		ResultSet resultSet=null;
		Vector<SigRecord> recordVec = null;
		try {
			System.out.print(DB_DRIVER);
			connection=DriverManager.getConnection(url,user,passwd);
			//第一次启动时用于创建足够多的列
			/*
			Statement stmt = connection.createStatement();
			Vector<String> strVec = new Vector<String>();
			for(int temp=201;temp<=250;temp++) {
				String query = new String();
				query = "ALTER TABLE PhraseAndCordinate ADD Cor" + temp +" VARCHAR NULL";
				strVec.add(query);
			}
		    for(int i=0;i<50;i++)  {
		    	stmt.executeUpdate(strVec.get(i));
		    }
		    */
			ps=connection.prepareStatement(sql);
			for(int i=0;i<paras.length;i++){
				ps.setString(i+1, paras[i]);
			}
			resultSet=ps.executeQuery();
			recordVec = this.rsToVec(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(resultSet!=null)
					resultSet.close();
				if(ps!=null)
					ps.close();
				if(connection!=null)
					connection.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return recordVec;
	}

	/**
	 * 将从数据库得到的结果集转为可使用的记录组
	 * @param resultSet 获取到的全部结果集
	 * @return recordVec 处理完的记录组
	 * @throws SQLException
	 */
	public Vector<SigRecord> rsToVec(ResultSet resultSet) throws SQLException{
		Vector<SigRecord> recordVec = new Vector<>();
		while(resultSet.next())
		{
			SigRecord sigRecord = new SigRecord();
			String rawPhrase = resultSet.getString(1);
			String phrase[] = null;
			Vector<String> phraseVec = new Vector<>();
			char[] head = new char[1];
			char[] tail = new char[1];
			int pLength = rawPhrase.length();
			rawPhrase.getChars(0, 1, head, 0);
			rawPhrase.getChars(pLength-1, pLength , tail, 0);
			int ascHead = head[0];
			int ascTail = tail[0];
			if(isEnglish(ascHead) && isEnglish(ascTail)) {
				phrase = rawPhrase.split(" ");
			} else{
				phrase = new String[3];
				phrase[0] = rawPhrase.substring(0, 2);
				phrase[1] = rawPhrase.substring(rawPhrase.length()-4, rawPhrase.length()-2);
				phrase[2] = rawPhrase.substring(rawPhrase.length()-2, rawPhrase.length());
			}
			int phraseLength = phrase.length;
			for(int i=0;i<phraseLength;i++) {
				phraseVec.add(phrase[i]);
			}
			sigRecord.setPhrase(phraseVec);
			Vector<Cordinate> cordinateVec = new Vector<>();
			for(int corIndex = 2;corIndex<=Parameters.corAmount && resultSet.getString(corIndex)!=null;corIndex++) {
				String[] cors = resultSet.getString(corIndex).split(" ");
				Cordinate cordinate = new Cordinate();
				cordinate.setX(Integer.valueOf(cors[0]));
				cordinate.setY(Integer.valueOf(cors[1]));
				cordinateVec.add(cordinate);
			}
			sigRecord.setCordinates(cordinateVec);
			recordVec.add(sigRecord);
		}
		return recordVec;
	}
	/**
	 * 用于判断当前的数据是否是在英文的阿斯克码范围内
	 * @param asc 用于判断的阿斯克码
	 * @return 返回判断的结果
	 */
	public boolean isEnglish(int asc) {
		if((asc >= 97 && asc<=122) ||(asc >= 65 && asc<=90)) return true;
		else return false;
	}

	@Override
	/**
	 * This method is used to update the database
	 * @param sql is an SQL statement that operates on the database which data is ?.The elements in Paras will replace the ? in SQL statement in order.
	 * @return When operating success return true, otherwise return false.
	 * @exception Database connection failed.
	 */
	public void sqlUpdate(String sql, String[] paras) {
		PreparedStatement ps=null;
		Connection ct=null;
		boolean b=true;
		try {
			Class.forName(DB_DRIVER);
			ct=DriverManager.getConnection(url);
			ps=ct.prepareStatement(sql);
			for(int i=0;i<paras.length;i++)
			{
				ps.setString(i+1, paras[i]);
			}
			ps.executeUpdate();
		} catch (Exception e) {
			b=false;
			e.printStackTrace();
		}finally{
			try {
				if(ps!=null)
					ps.close();
				if(ct!=null)
					ct.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
