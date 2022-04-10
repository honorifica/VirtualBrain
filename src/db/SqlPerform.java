package db;

import java.util.Vector;

import common.SigRecord;

/**
 * 数据库服务的接口
 * @author 姜云骞
 */
public interface SqlPerform {
	public Vector<SigRecord> allRecords(String sql,String []paras);
	public void sqlUpdate(String sql,String []paras);
}
