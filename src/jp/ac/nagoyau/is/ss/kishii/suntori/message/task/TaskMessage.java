package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import rescuecore2.worldmodel.EntityID;

/**
 * タスクを表現するためのメッセージクラスです．<br>
 * The TaskMessage is the message class express the task.
 * 
 * @author takefumi
 * 
 */
public abstract class TaskMessage extends RCRSCSMessage implements ITaskMessage {
	/**
	 * コンストラクタ<br>
	 * タスクを生成します．<br>
	 * <h2>Constructor</h2>Create the task.
	 * 
	 * @param type
	 * @param time
	 * @param ownerID
	 */
	public TaskMessage(BaseMessageType type, int time, EntityID ownerID) {
		super(type, time);
		this.setData(new EntityIDData(DataType.RESCUE_AGENT, ownerID));
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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

	@Deprecated
	protected void setEntityIDListData(DataType dType, List<EntityID> list) {
		super.setData(new EntityIDListData(dType, list));
	}

	/**
	 * タスク実行対象となるエージェント(救助隊)のEntityIDを取得します．<br>
	 * Return EntityID of rescue agent that have to execute this task.
	 * 
	 * 
	 * @return 救助隊のEntityIDを取得します．<br>
	 *         EntityID of rescue agent
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.PLATOON_AGENT, 0);
	}

	/**
	 * メッセージ送信者のEntityIDを取得します．<br>
	 * Return EntityID of the agent that sent this message.
	 * 
	 * @return　メッセージ送信者のEntityID<br>
	 *         EntityID of the agent(at, ac, pf, po, fb, fs)
	 */
	public EntityID getMessageOwnerID() {
		return super.getID(DataType.RESCUE_AGENT, 0);
	}
}
