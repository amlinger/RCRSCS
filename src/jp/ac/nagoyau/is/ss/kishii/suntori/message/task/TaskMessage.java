package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import rescuecore2.worldmodel.EntityID;

/**
 * タスクを表現するためのメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public abstract class TaskMessage extends RCRSCSMessage implements ITaskMessage {
	/**
	 * コンストラクタ<br>
	 * タスクを生成します．
	 * 
	 * @param type
	 * @param time
	 */
	public TaskMessage(BaseMessageType type, int time) {
		super(type, time);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public TaskMessage(BaseMessageType type, List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	protected void setEntityIDListData(DataType dType, List<EntityID> list) {
		super.setData(new EntityIDListData(dType, list));
	}

	/**
	 * タスク実行対象となるエージェント(救助隊)のEntityIDを取得します．
	 * 
	 * @return 救助隊のEntityIDを取得します．
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.PLATOON_AGENT, 0);
	}

}
