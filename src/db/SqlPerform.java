package db;

import java.util.Vector;

import common.SigRecord;

/**
 * ���ݿ����Ľӿ�
 * @author �����
 */
public interface SqlPerform {
	public Vector<SigRecord> allRecords(String sql,String []paras);
	public void sqlUpdate(String sql,String []paras);
}
