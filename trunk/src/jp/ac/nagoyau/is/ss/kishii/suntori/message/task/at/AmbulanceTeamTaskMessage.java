package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.at;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;
import rescuecore2.worldmodel.EntityID;

/**
 * 救急隊専用のタスクを表現するためのメッセージクラスです．<br>
 * The class represent the Task for ambulance team agent.
 * 
 * @author takefumi
 * 
 */
public abstract class AmbulanceTeamTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 救急隊専用タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>atID:救急隊のEntityID</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to give instructions to AT.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>atID:EntityID of AT that is ordered to do.</li>
	 * </ul>
	 * 
	 * @param type
	 *            メッセージの種類 <br>
	 *            message type
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param atID
	 *            救急隊のEntityID<br>
	 *            EntityID of AT
	 */
	public AmbulanceTeamTaskMessage(BaseMessageType type, int time,
			EntityID ownerID, EntityID atID) {
		super(type, time, ownerID);
		this.setData(new EntityIDData(DataType.AMBULANCE_TEAM, atID));
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
	public AmbulanceTeamTaskMessage(BaseMessageType type,
			List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * このタスクを実行すべき救急隊のEntityIDを取得します．<br>
	 * Return EntityID of AT that have to execute this task.
	 * 
	 * @return 救急隊のEntityID<br>
	 *         EntityID of AT
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.AMBULANCE_TEAM, 0);
	}

}
