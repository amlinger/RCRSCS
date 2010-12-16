package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.pf;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;
import rescuecore2.worldmodel.EntityID;

/**
 * 啓開隊専用タスクを表現するためのメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public abstract class PoliceForceTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 啓開隊専用タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>fbID:啓開隊のEntityID</li>
	 * </ul>
	 * 
	 * @param type
	 * @param time
	 * @param pfID
	 */
	public PoliceForceTaskMessage(BaseMessageType type, int time, EntityID pfID) {
		super(type, time);
		// this.setData(new ValueData(DataType.POLICE_FORCE, pfID.getValue()));
		this.setData(new EntityIDData(DataType.POLICE_FORCE, pfID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public PoliceForceTaskMessage(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * このタスクを実行すべき啓開隊のEntityIDを取得します．
	 * 
	 * @return 啓開隊のEntityID
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.POLICE_FORCE, 0);
	}
}
