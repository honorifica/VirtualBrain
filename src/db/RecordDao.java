package db;

import java.util.Vector;

import common.SigRecord;

/**
 * Dao服务的接口
 * @author 姜云骞
 *
 */
public interface RecordDao {
	Vector<SigRecord> getAllRecord();
}
