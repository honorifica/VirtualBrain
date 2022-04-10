package db;

import java.util.Vector;

import common.Cordinate;
import common.SigRecord;
/**
 * Dao����ķ��������ڵ���SQL�ķ��񷵻ؿɹ�ʹ�õ�Vector<SigRecord>
 * @author �����
 *
 */
public class RecordDaoImp implements RecordDao {
	@Override
	public Vector<SigRecord> getAllRecord() {
		 String sql="select * from PhraseAndCordinate";
	     return new SqlPerformImp().allRecords(sql, new String[] {});
	}
	public void addRecord(Vector<Cordinate> corVec, String phrase) {
		String sql = "insert into PhraseAndCordinate(Phrase";
		for(int i=1;i<corVec.size()+1;i++) {
			sql = sql + " ,Cor"+i+" ";
		}
		sql = sql + ") values ( ?";
		for(int i=0;i<corVec.size();i++) {
			sql = sql + ",?";
		}
		sql = sql+")";
		String[] paras = new String[corVec.size()+1];
		paras[0] = phrase;
		for(int i=1;i<corVec.size()+1;i++) {
			paras[i] = corVec.get(i - 1).getX()+" "+corVec.get(i - 1).getY();
		}
		new SqlPerformImp().sqlUpdate(sql, paras);
	}
}
