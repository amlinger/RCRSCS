package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.pf;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;
import rescuecore2.worldmodel.EntityID;

/**
 * 啓開隊専用タスクを表現するためのメッセージクラスです．<br>
 * The class represent the Task for Police Force agent.
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
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>fbID:啓開隊のEntityID</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to give instructions to PF.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>pfID:EntityID of PF that is ordered to do.</li>
	 * </ul>
	 * 
	 * @param type
	 *            メッセージの種類 <br>
	 *            message type
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param pfID
	 *            救急隊のEntityID<br>
	 *            EntityID of PF
	 */
	public PoliceForceTaskMessage(BaseMessageType type, int time,
			EntityID ownerID, EntityID pfID) {
		super(type, time, ownerID);
		// this.setData(new ValueData(DataType.POLICE_FORCE, pfID.getValue()));
		this.setData(new EntityIDData(DataType.POLICE_FORCE, pfID));
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
	public PoliceForceTaskMessage(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * このタスクを実行すべき啓開隊のEntityIDを取得します．<br>
	 * Return EntityID of PF that have to execute this task.
	 * 
	 * @return 啓開隊のEntityID<br>
	 *         EntityID of PF
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.POLICE_FORCE, 0);
	}
}
